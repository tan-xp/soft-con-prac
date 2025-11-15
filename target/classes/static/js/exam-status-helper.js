/**
 * 考试状态自动更新和交互助手
 */

const ExamStatusHelper = {
    // 状态自动刷新间隔（毫秒）
    autoRefreshInterval: null,
    autoRefreshTime: 30000, // 默认30秒刷新一次
    
    /**
     * 初始化考试状态自动更新
     */
    initAutoRefresh: function() {
        // 清除可能存在的旧计时器
        this.stopAutoRefresh();
        
        // 启动新的自动刷新
        this.autoRefreshInterval = setInterval(() => {
            this.updateAllExamStatuses();
        }, this.autoRefreshTime);
        
        console.log('考试状态自动刷新已启动，间隔时间：' + (this.autoRefreshTime / 1000) + '秒');
    },
    
    /**
     * 停止自动刷新
     */
    stopAutoRefresh: function() {
        if (this.autoRefreshInterval) {
            clearInterval(this.autoRefreshInterval);
            this.autoRefreshInterval = null;
            console.log('考试状态自动刷新已停止');
        }
    },
    
    /**
     * 更新所有考试的状态
     */
    updateAllExamStatuses: function() {
        // 优先使用examService，如果不可用则回退到直接axios调用
        if (window.examService && typeof window.examService.autoUpdateAllExamStatus === 'function') {
            window.examService.autoUpdateAllExamStatus()
                .then(result => {
                    if (result.success) {
                        console.log('考试状态自动更新成功，更新数量：' + result.data.length);
                        // 如果在列表页面，刷新当前页的数据
                        if (window.location.pathname.includes('/exam/list')) {
                            this.refreshExamList();
                        }
                    }
                })
                .catch(error => {
                    console.error('考试状态自动更新失败:', error);
                });
        } else {
            // 回退方案
            axios.get('/api/exams/auto-update-status')
                .then(response => {
                    if (response.data.success) {
                        console.log('考试状态自动更新成功，更新数量：' + response.data.data);
                        // 如果在列表页面，刷新当前页的数据
                        if (window.location.pathname.includes('/exam/list')) {
                            this.refreshExamList();
                        }
                    }
                })
                .catch(error => {
                    console.error('考试状态自动更新失败:', error);
                });
        }
    },
    
    /**
     * 刷新考试列表页面数据
     */
    refreshExamList: function() {
        // 检查页面是否存在刷新函数
        if (window.refreshExamTable) {
            window.refreshExamTable();
        }
    },
    
    /**
     * 手动更新单个考试的状态
     * @param {number} examId - 考试ID
     * @param {string} newStatus - 新状态
     * @param {function} successCallback - 成功回调
     * @param {function} errorCallback - 错误回调
     */
    updateExamStatus: function(examId, newStatus, successCallback, errorCallback) {
        // 优先使用examService
        if (window.examService && typeof window.examService.updateExamStatus === 'function') {
            window.examService.updateExamStatus(examId, newStatus)
                .then(result => {
                    if (result.success) {
                        if (successCallback) successCallback(result);
                        return true;
                    } else {
                        if (errorCallback) errorCallback(result);
                        return false;
                    }
                })
                .catch(error => {
                    console.error('更新考试状态失败:', error);
                    if (errorCallback) errorCallback({ message: '更新失败，请稍后重试' });
                    return false;
                });
        } else {
            // 回退方案
            axios.put(`/api/exams/${examId}/status?status=${newStatus}`)
                .then(response => {
                    if (response.data.success) {
                        if (successCallback) successCallback(response.data);
                        return true;
                    } else {
                        if (errorCallback) errorCallback(response.data);
                        return false;
                    }
                })
                .catch(error => {
                    console.error('更新考试状态失败:', error);
                    if (errorCallback) errorCallback({ message: '更新失败，请稍后重试' });
                    return false;
                });
        }
    },
    
    /**
     * 获取考试状态对应的样式类和显示文本
     * @param {string} status - 状态值
     * @param {string} startTime - 开始时间
     * @param {string} endTime - 结束时间
     * @returns {object} 包含样式类和显示文本的对象
     */
    getStatusInfo: function(status, startTime, endTime) {
        // 如果有明确的状态值，优先使用
        if (status) {
            switch(status) {
                case 'upcoming':
                    return { class: 'status-upcoming', text: '即将开始' };
                case 'ongoing':
                    return { class: 'status-ongoing', text: '进行中' };
                case 'ended':
                    return { class: 'status-ended', text: '已结束' };
                default:
                    return { class: 'status-unknown', text: '未知状态' };
            }
        }
        
        // 如果没有状态值，根据时间计算
        const now = new Date();
        const startDate = startTime ? new Date(startTime) : null;
        const endDate = endTime ? new Date(endTime) : null;
        
        if (!startDate || !endDate) {
            return { class: 'status-unknown', text: '未设置时间' };
        }
        
        if (now < startDate) {
            return { class: 'status-upcoming', text: '即将开始' };
        } else if (now >= startDate && now <= endDate) {
            return { class: 'status-ongoing', text: '进行中' };
        } else {
            return { class: 'status-ended', text: '已结束' };
        }
    },
    
    /**
     * 检查考试是否处于活动状态
     * @param {number} examId - 考试ID
     * @param {function} callback - 回调函数，参数为布尔值表示是否活动
     */
    checkExamActive: function(examId, callback) {
        // 优先使用examService
        if (window.examService && typeof window.examService.getExamStatus === 'function') {
            window.examService.getExamStatus(examId)
                .then(result => {
                    if (result.success) {
                        callback(result.data === 'IN_PROGRESS' || result.data === 'ongoing');
                    } else {
                        console.error('检查考试活动状态失败:', result.message);
                        callback(false);
                    }
                })
                .catch(error => {
                    console.error('检查考试活动状态请求失败:', error);
                    callback(false);
                });
        } else {
            // 回退方案
            axios.get(`/api/exams/${examId}/is-active`)
                .then(response => {
                    if (response.data.success) {
                        callback(response.data.data);
                    } else {
                        console.error('检查考试活动状态失败:', response.data.message);
                        callback(false);
                    }
                })
                .catch(error => {
                    console.error('检查考试活动状态请求失败:', error);
                    callback(false);
                });
        }
    },
    
    /**
     * 格式化考试剩余时间
     * @param {string} startTime - 开始时间
     * @param {string} endTime - 结束时间
     * @returns {string} 格式化的剩余时间文本
     */
    formatExamTimeRemaining: function(startTime, endTime) {
        const now = new Date();
        const startDate = startTime ? new Date(startTime) : null;
        const endDate = endTime ? new Date(endTime) : null;
        
        if (!startDate || !endDate) {
            return '时间未设置';
        }
        
        // 考试尚未开始
        if (now < startDate) {
            const diffMs = startDate - now;
            const days = Math.floor(diffMs / (1000 * 60 * 60 * 24));
            const hours = Math.floor((diffMs % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60));
            const minutes = Math.floor((diffMs % (1000 * 60 * 60)) / (1000 * 60));
            
            if (days > 0) {
                return `距离开始还有 ${days} 天 ${hours} 小时`;
            } else if (hours > 0) {
                return `距离开始还有 ${hours} 小时 ${minutes} 分钟`;
            } else {
                return `距离开始还有 ${minutes} 分钟`;
            }
        }
        // 考试进行中
        else if (now >= startDate && now <= endDate) {
            const diffMs = endDate - now;
            const hours = Math.floor(diffMs / (1000 * 60 * 60));
            const minutes = Math.floor((diffMs % (1000 * 60 * 60)) / (1000 * 60));
            
            if (hours > 0) {
                return `剩余 ${hours} 小时 ${minutes} 分钟`;
            } else {
                return `剩余 ${minutes} 分钟`;
            }
        }
        // 考试已结束
        else {
            return '已结束';
        }
    },
    
    /**
     * 初始化考试列表页的交互功能
     */
    initExamListInteractions: function() {
        // 启动自动刷新
        this.initAutoRefresh();
        
        // 添加页面离开时停止自动刷新的监听
        window.addEventListener('beforeunload', () => {
            this.stopAutoRefresh();
        });
        
        // 添加手动刷新按钮事件
        const refreshBtn = document.getElementById('refreshStatusBtn');
        if (refreshBtn) {
            refreshBtn.addEventListener('click', () => {
                refreshBtn.disabled = true;
                refreshBtn.textContent = '刷新中...';
                
                this.updateAllExamStatuses();
                
                setTimeout(() => {
                    refreshBtn.disabled = false;
                    refreshBtn.textContent = '刷新状态';
                }, 1000);
            });
        }
    },
    
    /**
     * 初始化考试详情页的交互功能
     */
    initExamDetailInteractions: function() {
        // 添加状态更新按钮事件
        const updateStatusBtn = document.getElementById('updateStatusBtn');
        const statusModal = document.getElementById('statusModal');
        
        if (updateStatusBtn && statusModal) {
            updateStatusBtn.addEventListener('click', () => {
                statusModal.style.display = 'flex';
            });
            
            // 添加关闭模态框的事件
            const closeButtons = statusModal.querySelectorAll('.close-modal, .btn-warning');
            closeButtons.forEach(btn => {
                btn.addEventListener('click', () => {
                    statusModal.style.display = 'none';
                });
            });
            
            // 点击模态框外部关闭
            statusModal.addEventListener('click', (e) => {
                if (e.target === statusModal) {
                    statusModal.style.display = 'none';
                }
            });
        }
    }
};

/**
 * 页面加载完成后初始化相应的交互功能
 */
window.addEventListener('DOMContentLoaded', () => {
    // 根据当前页面路径初始化相应功能
    const path = window.location.pathname;
    
    // 添加错误处理辅助函数
    window.handleAPIError = function(error) {
        console.error('API错误:', error);
        const errorMessage = error.response?.data?.message || error.message || '操作失败，请稍后重试';
        alert(errorMessage);
    };
    
    // 添加加载状态显示辅助函数
    window.showLoading = function(show, loadingElementId = 'loadingContainer') {
        const loadingElement = document.getElementById(loadingElementId);
        if (loadingElement) {
            loadingElement.style.display = show ? 'block' : 'none';
        }
    };
    
    if (path.includes('/exam/list')) {
        // 考试列表页面
        ExamStatusHelper.initExamListInteractions();
    } else if (path.includes('/exam/detail') || path.includes('/exam/detail/')) {
        // 考试详情页面
        ExamStatusHelper.initExamDetailInteractions();
    }
});