// 考试服务层 - 封装所有与ExamController相关的API交互
class ExamService {
    constructor() {
        this.baseUrl = '/api/exams';
        this.axios = window.axios;
        
        // 设置默认请求超时
        this.axios.defaults.timeout = 10000;
        
        // 请求拦截器，添加loading状态
        this.axios.interceptors.request.use(
            config => {
                // 可以在这里添加请求loading状态
                return config;
            },
            error => {
                return Promise.reject(error);
            }
        );
        
        // 响应拦截器，统一处理错误
        this.axios.interceptors.response.use(
            response => {
                return response.data;
            },
            error => {
                console.error('API请求错误:', error);
                // 统一错误处理
                if (error.response) {
                    switch (error.response.status) {
                        case 401:
                            // 未授权，可以跳转到登录页
                            alert('请先登录');
                            window.location.href = '/login';
                            break;
                        case 403:
                            alert('没有权限执行此操作');
                            break;
                        case 404:
                            alert('请求的资源不存在');
                            break;
                        case 500:
                            alert('服务器内部错误，请稍后重试');
                            break;
                        default:
                            alert(`请求失败: ${error.response.data.message || '未知错误'}`);
                    }
                } else if (error.request) {
                    alert('网络错误，请检查您的网络连接');
                } else {
                    alert('请求配置错误');
                }
                return Promise.reject(error);
            }
        );
    }

    /**
     * 获取所有考试
     * @returns {Promise}
     */
    getAllExams() {
        return this.axios.get(`${this.baseUrl}/all`);
    }

    /**
     * 分页获取考试
     * @param {number} page - 页码
     * @param {number} size - 每页大小
     * @returns {Promise}
     */
    getExamsByPage(page = 1, size = 10) {
        return this.axios.get(`${this.baseUrl}/page`, {
            params: { page, size }
        });
    }

    /**
     * 获取考试详情
     * @param {number} examId - 考试ID
     * @returns {Promise}
     */
    getExamById(examId) {
        return this.axios.get(`${this.baseUrl}/${examId}`);
    }

    /**
     * 使用已有试卷创建考试
     * @param {Object} examData - 考试数据
     * @returns {Promise}
     */
    createExamWithExistingPaper(examData) {
        return this.axios.post(`${this.baseUrl}/create-with-existing-paper`, examData);
    }

    /**
     * 自动组卷创建考试
     * @param {Object} examData - 考试数据，包含题型分布等信息
     * @returns {Promise}
     */
    createExamWithAutoPaper(examData) {
        return this.axios.post(`${this.baseUrl}/create-with-auto-paper`, examData);
    }

    /**
     * 更新考试
     * @param {number} examId - 考试ID
     * @param {Object} examData - 更新的考试数据
     * @returns {Promise}
     */
    updateExam(examId, examData) {
        return this.axios.put(`${this.baseUrl}/${examId}`, examData);
    }

    /**
     * 删除考试
     * @param {number} examId - 考试ID
     * @returns {Promise}
     */
    deleteExam(examId) {
        return this.axios.delete(`${this.baseUrl}/${examId}`);
    }

    /**
     * 更新考试状态
     * @param {number} examId - 考试ID
     * @param {string} status - 新状态
     * @returns {Promise}
     */
    updateExamStatus(examId, status) {
        return this.axios.put(`${this.baseUrl}/${examId}/status`, { status });
    }

    /**
     * 搜索考试
     * @param {string} keyword - 搜索关键词
     * @returns {Promise}
     */
    searchExams(keyword) {
        return this.axios.get(`${this.baseUrl}/search`, {
            params: { keyword }
        });
    }

    /**
     * 分页搜索考试
     * @param {string} keyword - 搜索关键词
     * @param {number} page - 页码
     * @param {number} size - 每页大小
     * @returns {Promise}
     */
    searchExamsByPage(keyword, page = 1, size = 10) {
        return this.axios.get(`${this.baseUrl}/search/page`, {
            params: { keyword, page, size }
        });
    }

    /**
     * 根据状态获取考试
     * @param {string} status - 考试状态
     * @returns {Promise}
     */
    getExamsByStatus(status) {
        return this.axios.get(`${this.baseUrl}/status/${status}`);
    }

    /**
     * 获取即将开始的考试
     * @returns {Promise}
     */
    getUpcomingExams() {
        return this.axios.get(`${this.baseUrl}/upcoming`);
    }

    /**
     * 获取进行中的考试
     * @returns {Promise}
     */
    getOngoingExams() {
        return this.axios.get(`${this.baseUrl}/ongoing`);
    }

    /**
     * 获取已结束的考试
     * @returns {Promise}
     */
    getEndedExams() {
        return this.axios.get(`${this.baseUrl}/ended`);
    }

    /**
     * 获取指定试卷的考试列表
     * @param {number} paperId - 试卷ID
     * @returns {Promise}
     */
    getExamsByPaperId(paperId) {
        return this.axios.get(`${this.baseUrl}/by-paper/${paperId}`);
    }

    /**
     * 自动更新所有考试状态
     * @returns {Promise}
     */
    autoUpdateAllExamStatus() {
        return this.axios.post(`${this.baseUrl}/auto-update-status`);
    }

    /**
     * 检查考试是否处于活动状态
     * @param {number} examId - 考试ID
     * @returns {Promise}
     */
    checkExamIsActive(examId) {
        return this.axios.get(`${this.baseUrl}/${examId}/is-active`);
    }

    /**
     * 获取考试统计信息
     * @param {number} examId - 考试ID
     * @returns {Promise}
     */
    getExamStatistics(examId) {
        // 注意：这里可能需要根据实际的API端点进行调整
        // 如果没有专门的统计API，可以使用自定义端点或处理现有数据
        return this.axios.get(`${this.baseUrl}/${examId}/statistics`);
    }

    /**
     * 批量操作考试（如批量删除）
     * @param {Object} operationData - 操作数据
     * @returns {Promise}
     */
    batchOperation(operationData) {
        return this.axios.post(`${this.baseUrl}/batch-operation`, operationData);
    }
}

// 创建单例实例
const examService = new ExamService();


// 为了兼容性，也挂载到window对象上
window.examService = examService;