package com.softcon.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;

@RestController
@Tag(name = "算式服务相关接口")
public class FormulaController {

    @GetMapping("/formula")
    @Operation(summary ="生成随机加减法题目列表")
    public List<String> randomlyGenerated(){
        return generate50Problems();
    }

    /**
     * 生成50道随机的加减法题
     * @return
     */
    private List<String> generate50Problems() {
        List<String> problems = new ArrayList<>();
        Set<String> problemSet = new HashSet<>();
        Random random = new Random();
        int count = 0;

        while (count < 53) {
            boolean isAddition = random.nextBoolean();
            int n1, n2, result;
            String expression;
            String key;

            if (isAddition) {
                // 生成加法题，确保和不超过100
                n1 = random.nextInt(100);
                n2 = random.nextInt(100 - n1) + 1; // 确保n2至少为1
                result = n1 + n2;
                expression = String.format("%d+%d=%d%" + (10 - (n1 + "+" + n2 + "=" + result).length()) + "s",
                        n1, n2, result, "");
                key = "+," + n1 + "," + n2 + "," + result;
            } else {
                // 生成减法题，确保结果非负
                n1 = random.nextInt(100) + 1; // 确保n1至少为1
                n2 = random.nextInt(n1 + 1); // 确保n2 <= n1
                result = n1 - n2;
                expression = String.format("%d-%d=%d%" + (10 - (n1 + "-" + n2 + "=" + result).length()) + "s",
                        n1, n2, result, "");
                key = "-," + n1 + "," + n2 + "," + result;
            }

            // 检查重复
            if (!problemSet.contains(key)) {
                problemSet.add(key);
                problems.add(expression);
                count++;
            }
        }

        return problems;
    }

    /**
     * 计算加减法
     * @param num1
     * @param num2
     * @param operation
     * @return
     */
    @GetMapping("/calculate")
    @Operation(summary ="计算加减法表达式")
    public Map<String, Object> calculate(
            @RequestParam("num1") double num1,
            @RequestParam("num2") double num2,
            @RequestParam("operation") String operation) {

        Map<String, Object> result = new HashMap<>();

        try {
            if ("+".equals(operation)) {
                double sum = num1 + num2;
                result.put("success", true);
                result.put("expression", String.format("%.2f + %.2f = %.2f", num1, num2, sum));
                result.put("result", sum);
            } else if ("-".equals(operation)) {
                if (num1 < num2) {
                    result.put("success", false);
                    result.put("message", "减法运算中，第一个数不能小于第二个数");
                    return result;
                }
                double diff = num1 - num2;
                result.put("success", true);
                result.put("expression", String.format("%.2f - %.2f = %.2f", num1, num2, diff));
                result.put("result", diff);
            } else {
                result.put("success", false);
                result.put("message", "不支持的运算类型");
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "计算出错：" + e.getMessage());
        }

        return result;
    }

    /**
     * 验证CSV文件中的算式答案
     * @param file
     * @return
     */
    @PostMapping("/verify")
    @ResponseBody
    @Operation(summary ="验证CSV中的算式答案")
    public ResponseEntity<Map<String, Object>> verifyCsv(@RequestParam("file") MultipartFile file) {
        Map<String, Object> result = new HashMap<>();
        List<Map<String, Object>> details = new ArrayList<>();
        int total = 0;
        int correct = 0;

        if (file.isEmpty()) {
            result.put("success", false);
            result.put("message", "请上传CSV文件");
            return ResponseEntity.badRequest().body(result);
        }

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            // 跳过表头
            reader.readLine();

            while ((line = reader.readLine()) != null) {
                total++;
                line = line.trim();
                if (line.isEmpty()) continue;

                Map<String, Object> item = new HashMap<>();
                item.put("expression", line);

                try {
                    // 解析格式："a+b=c" 或 "a-b=c"
                    boolean isAdd = line.contains("+");
                    String[] parts = isAdd ? line.split("[+=]") : line.split("[-=]");

                    if (parts.length != 3) {
                        item.put("correct", false);
                        item.put("message", "格式错误，正确格式：a+b=c 或 a-b=c");
                        details.add(item);
                        continue;
                    }

                    int num1 = Integer.parseInt(parts[0].trim());
                    int num2 = Integer.parseInt(parts[1].trim());
                    int userAnswer = Integer.parseInt(parts[2].trim());
                    int correctAnswer = isAdd ? num1 + num2 : num1 - num2;

                    boolean correctFlag = userAnswer == correctAnswer;
                    item.put("correct", correctFlag);
                    item.put("expected", correctAnswer);
                    item.put("userAnswer", userAnswer);

                    if (correctFlag) correct++;
                    details.add(item);
                } catch (Exception e) {
                    item.put("correct", false);
                    item.put("message", "解析错误：" + e.getMessage());
                    details.add(item);
                }
            }

            result.put("success", true);
            result.put("total", total);
            result.put("correct", correct);
            result.put("incorrect", total - correct);
            result.put("details", details);
            return ResponseEntity.ok(result);

        } catch (IOException e) {
            result.put("success", false);
            result.put("message", "文件读取失败：" + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
        }
    }

}

