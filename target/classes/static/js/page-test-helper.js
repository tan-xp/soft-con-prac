/**
 * 页面测试和性能优化助手模块
 * 提供页面加载检查、性能监控和功能验证功能
 */
const PageTestHelper = {
    /**
     * 页面加载状态监控
     */
    monitorPageLoad: function() {
        const startTime = performance.now();
        
        // 监控DOM加载完成
        if (document.readyState === 'loading') {
            document.addEventListener('DOMContentLoaded', () => {
                const domLoadTime = performance.now() - startTime;
                this.logPerformance('DOMContentLoaded', domLoadTime);
            });
        } else {
            const domLoadTime = performance.now() - startTime;
            this.logPerformance('DOMContentLoaded (already loaded)', domLoadTime);
        }
        
        // 监控页面完全加载
        window.addEventListener('load', () => {
            const loadTime = performance.now() - startTime;
            this.logPerformance('Window Load', loadTime);
        });
        
        // 检查关键资源加载
        this.checkCriticalResources();
    },
    
    /**
     * 检查关键资源加载状态
     */
    checkCriticalResources: function() {
        // 检查必要的CSS和JS文件是否加载
        const requiredJs = ['/js/axios.min.js', '/js/exam-service.js'];
        const requiredCss = [];
        
        // 检查JS文件
        requiredJs.forEach(src => {
            const found = Array.from(document.querySelectorAll('script')).some(script => 
                script.src.includes(src)
            );
            if (!found) {
                this.logWarning(`Required JS not found: ${src}`);
            }
        });
        
        // 检查CSS文件
        requiredCss.forEach(href => {
            const found = Array.from(document.querySelectorAll('link[rel="stylesheet"]')).some(link => 
                link.href.includes(href)
            );
            if (!found) {
                this.logWarning(`Required CSS not found: ${href}`);
            }
        });
    },
    
    /**
     * 验证页面功能完整性
     */
    validatePageFunctionality: function() {
        // 检查必要的DOM元素是否存在
        const criticalElements = this.getPageCriticalElements();
        
        criticalElements.forEach(element => {
            const el = document.querySelector(element.selector);
            if (!el) {
                this.logError(`Critical element not found: ${element.selector} (${element.description})`);
            }
        });
        
        // 检查必要的函数是否可用
        this.checkRequiredFunctions();
    },
    
    /**
     * 获取当前页面的关键元素
     */
    getPageCriticalElements: function() {
        const path = window.location.pathname;
        const elements = [];
        
        // 公共元素
        elements.push(
            { selector: '.navbar', description: '导航栏' },
            { selector: '.container', description: '主容器' }
        );
        
        // 根据页面路径添加特定元素
        if (path.includes('/exam/list')) {
            elements.push(
                { selector: '#examTable', description: '考试列表表格' },
                { selector: '.search-bar', description: '搜索栏' },
                { selector: '#addExamBtn', description: '添加考试按钮' }
            );
        } else if (path.includes('/exam/detail')) {
            elements.push(
                { selector: '.exam-info', description: '考试信息区域' },
                { selector: '#updateStatusBtn', description: '更新状态按钮' }
            );
        } else if (path.includes('/exam/create') || path.includes('/exam/edit')) {
            elements.push(
                { selector: '#examForm', description: '考试表单' },
                { selector: 'button[type="submit"]', description: '提交按钮' }
            );
        } else if (path.includes('/exam/statistics')) {
            elements.push(
                { selector: '#examSelect', description: '考试选择下拉框' },
                { selector: '.tab-content', description: '统计数据标签内容' }
            );
        }
        
        return elements;
    },
    
    /**
     * 检查必要的函数是否可用
     */
    checkRequiredFunctions: function() {
        const requiredFunctions = [];
        const path = window.location.pathname;
        
        if (path.includes('/exam/list')) {
            requiredFunctions.push('fetchExams', 'deleteExam', 'updateExamStatus');
        } else if (path.includes('/exam/detail')) {
            requiredFunctions.push('fetchExamDetails', 'updateExamStatus', 'deleteExam');
        } else if (path.includes('/exam/create')) {
            requiredFunctions.push('loadAvailablePapers', 'submitExamForm');
        } else if (path.includes('/exam/edit')) {
            requiredFunctions.push('loadExamData', 'saveExam');
        }
        
        requiredFunctions.forEach(funcName => {
            if (typeof window[funcName] !== 'function') {
                this.logWarning(`Required function not found: ${funcName}`);
            }
        });
        
        // 检查核心服务是否可用
        if (typeof examService === 'undefined') {
            this.logError('examService is not available');
        }
    },
    
    /**
     * 性能日志记录
     */
    logPerformance: function(metric, value) {
        console.log(`Performance: ${metric} - ${value.toFixed(2)}ms`);
    },
    
    /**
     * 警告日志记录
     */
    logWarning: function(message) {
        console.warn(`[PageTest] Warning: ${message}`);
    },
    
    /**
     * 错误日志记录
     */
    logError: function(message) {
        console.error(`[PageTest] Error: ${message}`);
    },
    
    /**
     * 应用常见性能优化
     */
    applyOptimizations: function() {
        // 延迟加载非关键资源
        this.deferNonCriticalResources();
        
        // 优化大型表格渲染
        this.optimizeTableRendering();
        
        // 优化动画性能
        this.optimizeAnimations();
    },
    
    /**
     * 延迟加载非关键资源
     */
    deferNonCriticalResources: function() {
        // 将非关键脚本标记为defer
        const nonCriticalScripts = document.querySelectorAll('script:not([defer]):not([src*="axios"]):not([src*="exam-service"])');
        nonCriticalScripts.forEach(script => {
            if (!script.src.includes('echarts') && !script.src.includes('jquery') && !script.src.includes('bootstrap')) {
                script.defer = true;
            }
        });
    },
    
    /**
     * 优化表格渲染
     */
    optimizeTableRendering: function() {
        const tables = document.querySelectorAll('table');
        tables.forEach(table => {
            // 为表格添加固定头部
            if (table.id === 'examTable' || table.classList.contains('data-table')) {
                table.classList.add('optimized-table');
                // 可以在这里添加更多表格优化逻辑
            }
        });
    },
    
    /**
     * 优化动画性能
     */
    optimizeAnimations: function() {
        // 使用requestAnimationFrame优化动画
        if (typeof window.smoothScroll !== 'undefined') {
            const originalScroll = window.smoothScroll;
            window.smoothScroll = function(target, duration) {
                requestAnimationFrame(() => {
                    originalScroll(target, duration);
                });
            };
        }
    },
    
    /**
     * 初始化页面测试
     */
    init: function() {
        // 仅在开发环境运行测试
        if (window.location.hostname === 'localhost' || window.location.hostname === '127.0.0.1') {
            console.log('Initializing page tests...');
            
            // 监控页面加载
            this.monitorPageLoad();
            
            // 页面加载完成后验证功能
            window.addEventListener('DOMContentLoaded', () => {
                setTimeout(() => {
                    this.validatePageFunctionality();
                    this.applyOptimizations();
                    console.log('Page tests completed.');
                }, 1000); // 延迟1秒执行，确保所有资源都已加载
            });
        }
    }
};

// 初始化页面测试助手
window.addEventListener('load', () => {
    PageTestHelper.init();
});