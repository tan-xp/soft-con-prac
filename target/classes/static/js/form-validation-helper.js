/**
 * 表单验证助手模块
 * 提供统一的表单验证和错误提示功能
 */
const FormValidationHelper = {
    /**
     * 验证考试创建/编辑表单
     * @param {HTMLFormElement} formElement - 表单元素
     * @returns {boolean} - 是否验证通过
     */
    validateExamForm: function(formElement) {
        let isValid = true;
        
        // 清除之前的错误提示
        this.clearFormErrors(formElement);
        
        // 验证考试名称
        const examName = formElement.querySelector('input[name="name"]')?.value.trim();
        if (!examName) {
            this.showFieldError(formElement.querySelector('input[name="name"]'), '考试名称不能为空');
            isValid = false;
        } else if (examName.length > 100) {
            this.showFieldError(formElement.querySelector('input[name="name"]'), '考试名称不能超过100个字符');
            isValid = false;
        }
        
        // 验证开始时间
        const startTime = formElement.querySelector('input[name="startTime"]')?.value;
        if (!startTime) {
            this.showFieldError(formElement.querySelector('input[name="startTime"]'), '开始时间不能为空');
            isValid = false;
        }
        
        // 验证结束时间
        const endTime = formElement.querySelector('input[name="endTime"]')?.value;
        if (!endTime) {
            this.showFieldError(formElement.querySelector('input[name="endTime"]'), '结束时间不能为空');
            isValid = false;
        }
        
        // 验证开始时间和结束时间的逻辑关系
        if (startTime && endTime) {
            const startDate = new Date(startTime);
            const endDate = new Date(endTime);
            const now = new Date();
            
            if (startDate >= endDate) {
                this.showFieldError(formElement.querySelector('input[name="endTime"]'), '结束时间必须晚于开始时间');
                isValid = false;
            }
            
            if (endDate <= now) {
                this.showFieldError(formElement.querySelector('input[name="endTime"]'), '结束时间不能早于当前时间');
                isValid = false;
            }
        }
        
        // 验证考试时长（如果存在）
        const duration = formElement.querySelector('input[name="duration"]')?.value;
        if (duration !== undefined && duration !== null && duration !== '') {
            const durationNum = parseInt(duration);
            if (isNaN(durationNum) || durationNum <= 0 || durationNum > 480) {
                this.showFieldError(formElement.querySelector('input[name="duration"]'), '考试时长必须在1-480分钟之间');
                isValid = false;
            }
        }
        
        // 验证总分（如果存在）
        const totalScore = formElement.querySelector('input[name="totalScore"]')?.value;
        if (totalScore !== undefined && totalScore !== null && totalScore !== '') {
            const scoreNum = parseFloat(totalScore);
            if (isNaN(scoreNum) || scoreNum <= 0) {
                this.showFieldError(formElement.querySelector('input[name="totalScore"]'), '总分必须大于0');
                isValid = false;
            }
        }
        
        // 验证试卷选择（如果是创建考试）
        const paperId = formElement.querySelector('select[name="paperId"]')?.value || 
                        formElement.querySelector('input[name="paperId"]')?.value;
        if (paperId === '' || paperId === null || paperId === undefined) {
            const paperElement = formElement.querySelector('select[name="paperId"]') || 
                                formElement.querySelector('input[name="paperId"]');
            if (paperElement) {
                this.showFieldError(paperElement, '请选择或输入试卷ID');
                isValid = false;
            }
        }
        
        return isValid;
    },
    
    /**
     * 验证搜索表单
     * @param {HTMLFormElement} formElement - 表单元素
     * @returns {boolean} - 是否验证通过
     */
    validateSearchForm: function(formElement) {
        let isValid = true;
        
        // 清除之前的错误提示
        this.clearFormErrors(formElement);
        
        // 验证日期范围（如果存在）
        const startDate = formElement.querySelector('input[name="startDate"]')?.value;
        const endDate = formElement.querySelector('input[name="endDate"]')?.value;
        
        if (startDate && endDate) {
            const start = new Date(startDate);
            const end = new Date(endDate);
            
            if (start > end) {
                this.showFieldError(formElement.querySelector('input[name="endDate"]'), '结束日期不能早于开始日期');
                isValid = false;
            }
        }
        
        return isValid;
    },
    
    /**
     * 显示字段错误
     * @param {HTMLElement} fieldElement - 字段元素
     * @param {string} errorMessage - 错误消息
     */
    showFieldError: function(fieldElement, errorMessage) {
        if (!fieldElement) return;
        
        // 给字段添加错误样式
        fieldElement.classList.add('is-invalid');
        
        // 查找或创建错误提示元素
        let errorElement = fieldElement.nextElementSibling;
        if (!errorElement || !errorElement.classList.contains('invalid-feedback')) {
            errorElement = document.createElement('div');
            errorElement.className = 'invalid-feedback';
            fieldElement.parentNode.appendChild(errorElement);
        }
        
        // 设置错误消息
        errorElement.textContent = errorMessage;
        
        // 自动聚焦到错误字段
        fieldElement.focus();
    },
    
    /**
     * 清除表单所有错误
     * @param {HTMLFormElement} formElement - 表单元素
     */
    clearFormErrors: function(formElement) {
        if (!formElement) return;
        
        // 移除所有字段的错误样式
        const invalidFields = formElement.querySelectorAll('.is-invalid');
        invalidFields.forEach(field => {
            field.classList.remove('is-invalid');
        });
        
        // 清除所有错误提示
        const errorMessages = formElement.querySelectorAll('.invalid-feedback');
        errorMessages.forEach(error => {
            error.textContent = '';
        });
    },
    
    /**
     * 显示表单级别的错误消息
     * @param {string} message - 错误消息
     * @param {string} containerId - 容器ID，默认使用alert-container
     */
    showFormError: function(message, containerId = 'alert-container') {
        let container = document.getElementById(containerId);
        
        if (!container) {
            // 如果容器不存在，创建一个
            container = document.createElement('div');
            container.id = containerId;
            container.className = 'mt-3';
            document.body.insertBefore(container, document.body.firstChild);
        }
        
        // 创建错误提示元素
        const alert = document.createElement('div');
        alert.className = 'alert alert-danger alert-dismissible fade show';
        alert.role = 'alert';
        alert.innerHTML = `
            ${message}
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        `;
        
        // 清空容器并添加新的错误提示
        container.innerHTML = '';
        container.appendChild(alert);
        
        // 5秒后自动关闭
        setTimeout(() => {
            alert.classList.remove('show');
            alert.classList.add('fade');
            setTimeout(() => {
                if (alert.parentNode === container) {
                    container.removeChild(alert);
                }
            }, 150);
        }, 5000);
    },
    
    /**
     * 显示表单级别的成功消息
     * @param {string} message - 成功消息
     * @param {string} containerId - 容器ID，默认使用alert-container
     */
    showFormSuccess: function(message, containerId = 'alert-container') {
        let container = document.getElementById(containerId);
        
        if (!container) {
            // 如果容器不存在，创建一个
            container = document.createElement('div');
            container.id = containerId;
            container.className = 'mt-3';
            document.body.insertBefore(container, document.body.firstChild);
        }
        
        // 创建成功提示元素
        const alert = document.createElement('div');
        alert.className = 'alert alert-success alert-dismissible fade show';
        alert.role = 'alert';
        alert.innerHTML = `
            ${message}
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        `;
        
        // 清空容器并添加新的成功提示
        container.innerHTML = '';
        container.appendChild(alert);
        
        // 3秒后自动关闭
        setTimeout(() => {
            alert.classList.remove('show');
            alert.classList.add('fade');
            setTimeout(() => {
                if (alert.parentNode === container) {
                    container.removeChild(alert);
                }
            }, 150);
        }, 3000);
    },
    
    /**
     * 添加表单实时验证
     * @param {HTMLFormElement} formElement - 表单元素
     */
    addLiveValidation: function(formElement) {
        if (!formElement) return;
        
        // 为所有输入字段添加验证事件
        const inputFields = formElement.querySelectorAll('input, select, textarea');
        inputFields.forEach(field => {
            // 移除旧的事件监听器
            field.removeEventListener('input', this.handleInputValidation.bind(this));
            field.removeEventListener('blur', this.handleBlurValidation.bind(this));
            
            // 添加新的事件监听器
            field.addEventListener('input', this.handleInputValidation.bind(this));
            field.addEventListener('blur', this.handleBlurValidation.bind(this));
        });
    },
    
    /**
     * 处理输入时的验证
     * @param {Event} event - 事件对象
     */
    handleInputValidation: function(event) {
        const field = event.target;
        
        // 输入时自动清除错误状态
        if (field.classList.contains('is-invalid')) {
            field.classList.remove('is-invalid');
            const errorElement = field.nextElementSibling;
            if (errorElement && errorElement.classList.contains('invalid-feedback')) {
                errorElement.textContent = '';
            }
        }
    },
    
    /**
     * 处理失焦时的验证
     * @param {Event} event - 事件对象
     */
    handleBlurValidation: function(event) {
        const field = event.target;
        const form = field.closest('form');
        
        // 根据字段类型进行验证
        if (form && field.name === 'name') {
            if (!field.value.trim()) {
                this.showFieldError(field, '此字段不能为空');
            } else if (field.value.length > 100) {
                this.showFieldError(field, '字符长度不能超过100');
            }
        } else if (field.type === 'number' || field.name === 'duration' || field.name === 'totalScore') {
            const value = parseFloat(field.value);
            if (!isNaN(value) && value <= 0) {
                this.showFieldError(field, '请输入大于0的数值');
            }
        } else if (field.type === 'datetime-local' || field.name === 'startTime' || field.name === 'endTime') {
            if (field.value) {
                // 可以添加日期时间的格式验证
            }
        }
    },
    
    /**
     * 初始化表单验证
     * @param {string|HTMLFormElement} formSelector - 表单选择器或表单元素
     * @param {Object} options - 配置选项
     */
    initFormValidation: function(formSelector, options = {}) {
        const form = typeof formSelector === 'string' ? document.querySelector(formSelector) : formSelector;
        
        if (!form || !(form instanceof HTMLFormElement)) {
            console.warn('表单元素不存在或无效');
            return;
        }
        
        // 添加实时验证
        if (options.liveValidation !== false) {
            this.addLiveValidation(form);
        }
        
        // 添加提交事件处理
        form.addEventListener('submit', (event) => {
            // 根据表单ID或类名确定验证类型
            let isValid = true;
            
            if (form.id === 'examForm' || form.classList.contains('exam-form')) {
                isValid = this.validateExamForm(form);
            } else if (form.id === 'searchForm' || form.classList.contains('search-form')) {
                isValid = this.validateSearchForm(form);
            }
            
            // 如果验证失败，阻止表单提交
            if (!isValid) {
                event.preventDefault();
                event.stopPropagation();
                this.showFormError('表单验证失败，请检查并修正错误后重试');
            }
        });
    }
};

/**
 * 扩展ExamService以添加统一的错误处理
 */
if (typeof examService !== 'undefined') {
    // 保存原始的请求方法
    const originalRequest = examService._request;
    
    // 重写请求方法，添加统一的错误处理
    examService._request = function(config) {
        return originalRequest.call(this, config)
            .catch(error => {
                // 使用FormValidationHelper显示错误
                if (FormValidationHelper && typeof FormValidationHelper.showFormError === 'function') {
                    const errorMessage = error.response?.data?.message || 
                                        error.message || 
                                        '网络请求失败，请检查网络连接或稍后重试';
                    FormValidationHelper.showFormError(errorMessage);
                }
                // 重新抛出错误，让调用者可以继续处理
                throw error;
            });
    };
}

/**
 * 页面加载完成后初始化表单验证
 */
window.addEventListener('DOMContentLoaded', () => {
    // 初始化所有表单的验证
    document.querySelectorAll('form').forEach(form => {
        FormValidationHelper.initFormValidation(form, {
            liveValidation: true
        });
    });
});