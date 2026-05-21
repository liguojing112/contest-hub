/**
 * 日志级别枚举
 */
const LogLevel = {
    DEBUG: 'DEBUG',
    INFO: 'INFO',
    WARN: 'WARN',
    ERROR: 'ERROR'
};

/**
 * 日志工具类
 */
const Logger = {
    /**
     * 记录日志
     */
    log: function(level, action, details = {}) {
        const logEntry = {
            timestamp: new Date().toISOString(),
            level: level,
            action: action,
            details: typeof details === 'string' ? { message: details } : details,
            user: this.getCurrentUserInfo(),
            page: window.location.pathname,
            userAgent: navigator.userAgent
        };

        // 控制台输出
        const prefixes = {
            [LogLevel.DEBUG]: '🔍',
            [LogLevel.INFO]: '📄',
            [LogLevel.WARN]: '⚠️',
            [LogLevel.ERROR]: '❌'
        };

        const colorStyles = {
            [LogLevel.DEBUG]: 'color: #6B7280',
            [LogLevel.INFO]: 'color: #3B82F6',
            [LogLevel.WARN]: 'color: #F59E0B',
            [LogLevel.ERROR]: 'color: #EF4444'
        };

        console.log(`%c${prefixes[level]} [${level}] ${action}`, colorStyles[level], logEntry);

        // 本地存储日志
        this.storeLog(logEntry);

        // 发送日志到服务器（如果后端可用）
        this.sendLogToServer(logEntry);
    },

    /**
     * DEBUG级别日志
     */
    debug: function(action, details) {
        this.log(LogLevel.DEBUG, action, details);
    },

    /**
     * INFO级别日志
     */
    info: function(action, details) {
        this.log(LogLevel.INFO, action, details);
    },

    /**
     * WARN级别日志
     */
    warn: function(action, details) {
        this.log(LogLevel.WARN, action, details);
    },

    /**
     * ERROR级别日志
     */
    error: function(action, details) {
        this.log(LogLevel.ERROR, action, details);
    },

    /**
     * 获取当前用户信息
     */
    getCurrentUserInfo: function() {
        try {
            const userInfo = localStorage.getItem('userInfo');
            return userInfo ? JSON.parse(userInfo) : { username: 'anonymous', role: 'unknown' };
        } catch (e) {
            return { username: 'anonymous', role: 'unknown' };
        }
    },

    /**
     * 存储日志到本地
     */
    storeLog: function(logEntry) {
        try {
            const logs = JSON.parse(localStorage.getItem('systemLogs') || '[]');
            logs.push(logEntry);
            
            // 最多保留1000条日志
            if (logs.length > 1000) {
                logs.shift();
            }
            
            localStorage.setItem('systemLogs', JSON.stringify(logs));
        } catch (e) {
            console.error('存储日志失败:', e);
        }
    },

    /**
     * 发送日志到服务器
     */
    sendLogToServer: async function(logEntry) {
        try {
            const token = localStorage.getItem('token');
            await fetch(`${API_BASE_URL}/logs`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    ...(token ? { 'Authorization': `Bearer ${token}` } : {})
                },
                body: JSON.stringify(logEntry),
                keepalive: true
            });
        } catch (e) {
            // 忽略发送失败，日志已存储在本地
        }
    },

    /**
     * 获取日志列表
     */
    getLogs: function() {
        try {
            return JSON.parse(localStorage.getItem('systemLogs') || '[]');
        } catch (e) {
            return [];
        }
    },

    /**
     * 清除日志
     */
    clearLogs: function() {
        localStorage.removeItem('systemLogs');
    }
};

/**
 * 通用API工具类
 * 用于所有页面与后端交互
 */
const API_BASE_URL = 'http://localhost:8080/api';

/**
 * 获取Token
 */
function getToken() {
    return localStorage.getItem('token') || '';
}

/**
 * 获取当前用户信息
 */
function getCurrentUser() {
    try {
        return JSON.parse(localStorage.getItem('userInfo') || '{}');
    } catch (error) {
        Logger.error('获取用户信息失败', error);
        return {};
    }
}

/**
 * 通用请求函数
 */
async function apiRequest(url, options = {}) {
    const token = getToken();
    const headers = {
        'Content-Type': 'application/json',
        ...options.headers
    };

    if (token) {
        headers['Authorization'] = `Bearer ${token}`;
    }

    const startTime = Date.now();
    Logger.info('API请求开始', { 
        method: options.method || 'GET', 
        url: `${API_BASE_URL}${url}`,
        hasToken: !!token 
    });

    try {
        const response = await fetch(`${API_BASE_URL}${url}`, {
            ...options,
            headers
        });

        const duration = Date.now() - startTime;
        Logger.info('API响应完成', { 
            status: response.status, 
            duration: `${duration}ms` 
        });

        if (!response.ok) {
            let errorMessage = '请求失败';
            try {
                const errorData = await response.json();
                errorMessage = errorData.message || errorMessage;
            } catch (e) {
                // 忽略解析错误
            }
            Logger.error('API请求失败', { 
                status: response.status, 
                message: errorMessage 
            });
            throw new Error(errorMessage);
        }

        const result = await response.json();
        Logger.debug('API响应数据', result);
        return result;
    } catch (error) {
        Logger.error('API请求异常', { message: error.message, stack: error.stack });
        throw error;
    }
}

/**
 * 认证相关API
 */
const AuthAPI = {
    /**
     * 登录
     */
    login: async (username, password) => {
        return apiRequest('/auth/login', {
            method: 'POST',
            body: JSON.stringify({ username, password })
        });
    },

    /**
     * 注册
     */
    register: async (userData) => {
        return apiRequest('/auth/register', {
            method: 'POST',
            body: JSON.stringify(userData)
        });
    },

    /**
     * 检查用户名是否存在
     */
    checkUsername: async (username) => {
        return apiRequest(`/auth/check-username?username=${encodeURIComponent(username)}`);
    }
};

/**
 * 竞赛相关API
 */
const CompetitionAPI = {
    /**
     * 获取竞赛列表
     */
    list: async (params = {}) => {
        const query = new URLSearchParams(params).toString();
        const url = query ? `/competitions?${query}` : '/competitions';
        return apiRequest(url);
    },

    /**
     * 获取竞赛详情
     */
    get: async (id) => {
        return apiRequest(`/competitions/${id}`);
    },

    /**
     * 创建竞赛
     */
    create: async (competitionData) => {
        return apiRequest('/competitions', {
            method: 'POST',
            body: JSON.stringify(competitionData)
        });
    },

    /**
     * 更新竞赛
     */
    update: async (id, competitionData) => {
        return apiRequest(`/competitions/${id}`, {
            method: 'PUT',
            body: JSON.stringify(competitionData)
        });
    },

    /**
     * 删除竞赛
     */
    delete: async (id) => {
        return apiRequest(`/competitions/${id}`, {
            method: 'DELETE'
        });
    },

    /**
     * 获取我的竞赛
     */
    my: async () => {
        return apiRequest('/competitions/my');
    },

    /**
     * 获取活跃竞赛
     */
    active: async () => {
        return apiRequest('/competitions/active');
    }
};

/**
 * 报名相关API
 */
const RegistrationAPI = {
    /**
     * 获取我的报名
     */
    my: async () => {
        return apiRequest('/registrations');
    },

    /**
     * 报名竞赛
     */
    create: async (registrationData) => {
        return apiRequest('/registrations', {
            method: 'POST',
            body: JSON.stringify(registrationData)
        });
    },

    /**
     * 取消报名
     */
    delete: async (id) => {
        return apiRequest(`/registrations/${id}`, {
            method: 'DELETE'
        });
    }
};

/**
 * 消息相关API
 */
const MessageAPI = {
    /**
     * 获取我的消息
     */
    list: async () => {
        return apiRequest('/messages');
    },

    /**
     * 获取未读消息数
     */
    unreadCount: async () => {
        return apiRequest('/messages/unread-count');
    },

    /**
     * 标记消息为已读
     */
    markRead: async (id) => {
        return apiRequest(`/messages/${id}/read`, {
            method: 'POST'
        });
    },

    /**
     * 批量标记已读
     */
    markAllRead: async () => {
        return apiRequest('/messages/read-all', {
            method: 'POST'
        });
    },

    /**
     * 删除消息
     */
    delete: async (id) => {
        return apiRequest(`/messages/${id}`, {
            method: 'DELETE'
        });
    },

    /**
     * 发送消息
     */
    send: async (messageData) => {
        return apiRequest('/messages', {
            method: 'POST',
            body: JSON.stringify(messageData)
        });
    }
};

/**
 * 管理员API
 */
const AdminAPI = {
    /**
     * 获取用户列表
     */
    listUsers: async (params = {}) => {
        const query = new URLSearchParams(params).toString();
        const url = query ? `/admin/users?${query}` : '/admin/users';
        return apiRequest(url);
    },

    /**
     * 获取用户详情
     */
    getUser: async (id) => {
        return apiRequest(`/admin/users/${id}`);
    },

    /**
     * 更新用户
     */
    updateUser: async (id, userData) => {
        return apiRequest(`/admin/users/${id}`, {
            method: 'PUT',
            body: JSON.stringify(userData)
        });
    },

    /**
     * 删除用户
     */
    deleteUser: async (id) => {
        return apiRequest(`/admin/users/${id}`, {
            method: 'DELETE'
        });
    },

    /**
     * 获取统计数据
     */
    getStats: async () => {
        return apiRequest('/admin/stats');
    },

    /**
     * 更新竞赛状态
     */
    updateCompetitionStatus: async (id, status) => {
        return apiRequest(`/admin/competitions/${id}/status`, {
            method: 'POST',
            body: JSON.stringify({ status })
        });
    }
};

// 导出API对象
if (typeof module !== 'undefined' && module.exports) {
    module.exports = {
        API_BASE_URL,
        getToken,
        getCurrentUser,
        apiRequest,
        AuthAPI,
        CompetitionAPI,
        RegistrationAPI,
        MessageAPI,
        AdminAPI
    };
}
