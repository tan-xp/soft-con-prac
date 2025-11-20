package com.softcon.pojo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 后端统一返回结果
 * @param <T>
 */
@Data
public class Result<T> implements Serializable {

    @Schema(description = "编码")
    private Boolean success;

    @Schema(description = "错误信息")
    private String msg;

    @Schema(description = "数据")
    private T data;

    /**
     * 成功结果的静态方法生成器。
     *
     * @param <T> 结果数据的泛型类型。
     * @return 新创建的表示成功结果的对象。
     */
    public static <T> Result<T> success() {
        Result<T> result = new Result<T>();
        result.success = true;
        return result;
    }

    /**
     * 成功结果带数据的静态方法生成器。
     *
     * @param <T> 结果数据的泛型类型。
     * @param object 操作成功的数据。
     * @return 新创建的包含成功数据的结果对象。
     */
    public static <T> Result<T> success(T object) {
        Result<T> result = new Result<T>();
        result.data = object;
        result.success = true;
        return result;
    }

    /**
     * 成功结果带数据和消息的静态方法生成器。
     *
     * @param <T> 结果数据的泛型类型。
     * @param object 操作成功的数据。
     * @param msg 操作成功的详细信息。
     * @return 新创建的包含成功数据的结果对象。
     */
    public static <T> Result<T> success(T object,String msg) {
        Result<T> result = new Result<T>();
        result.data = object;
        result.success = true;
        result.msg = msg;
        return result;
    }

    /**
     * 成功结果的静态方法生成器。
     *
     * @param msg 操作成功的详细信息。
     * @param <T> 结果数据的泛型类型。
     * @return 新创建的表示成功结果的对象。
     */
    public static <T> Result<T> success(String msg) {
        Result result = new Result();
        result.msg = msg;
        result.success = true;
        return result;
    }

    /**
     * 错误结果的静态方法生成器。
     *
     * @param msg 操作失败的详细信息。
     * @param <T> 结果数据的泛型类型。
     * @return 新创建的表示失败结果的对象。
     */
    public static <T> Result<T> error(String msg) {
        Result result = new Result();
        result.msg = msg;
        result.success = false;
        return result;
    }

}
