import axios from 'axios';

const API_URL = '/api/v1';

const api = axios.create({
    baseURL: API_URL,
    headers: {
        'Content-Type': 'application/json'
    },
    withCredentials: true
});

api.interceptors.request.use(
    (config) => {
        const token = localStorage.getItem('token');
        if (token) {
            config.headers.Authorization = `Bearer ${token}`;
        }
        return config;
    },
    (error) => Promise.reject(error)
);

api.interceptors.response.use(
    (response) => response,
    (error) => {
        if (error.response?.status === 401) {
            localStorage.removeItem('token');
            if (window.location.pathname !== '/login') {
                window.location.href = '/login';
            }
        }
        return Promise.reject(error);
    }
);

export const authAPI = {
    login: (email, password) => api.post('/auth/login', { email, password }),
    register: (data) => api.post('/auth/registration', data),
    forgotPassword: (email) => api.post('/auth/forgot-password', { email }),
    resetPassword: (token, newPassword) => api.post('/auth/reset-password', { token, newPassword }),
    logout: () => api.post('/auth/logout')
};

export const userAPI = {
    getCurrentUser: () => api.get('/users/me'),
    updateCurrentUser: (data) => api.put('/users/me', data),
    deleteCurrentUser: () => api.delete('/users/me')
};

export const nutritionAPI = {
    generateReport: (data) => api.post('/nutrition/report', data),
    getReports: () => api.get('/nutrition/reports'),
    deleteReport: (id) => api.delete(`/nutrition/reports/${id}`)
};

export const systemAPI = {
    generateRecommendations: () => api.post('/recommendations/generate'),
    saveResults: (data) => api.post('/nutrition/report/complete', data)
};

export const dietAPI = {
    getUserDiets: () => api.get('/diets'),
    getById: (id) => api.get(`/diets/${id}`),
    create: (productIds) => api.post('/diets', { productIds }),
    delete: (id) => api.delete(`/diets/${id}`)
};

export const recommendationAPI = {
    getUserRecommendations: () => api.get('/recommendations'),
    getById: (id) => api.get(`/recommendations/${id}`),
    delete: (id) => api.delete(`/recommendations/${id}`)
};

export default api;
