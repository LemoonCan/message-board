import axios from 'axios';
import config from '../config';

// 从配置中获取API URL
const API_URL = config.API_URL;

export interface LoginRequest {
  name: string;
  password: string;
  rememberMe?: boolean;
}

export interface RegisterRequest {
  name: string;
  email: string;
  password: string;
}

export interface User {
  id: number;
  username: string;
  email: string;
}

// 用户登录
export const login = async (data: LoginRequest): Promise<User> => {
  try {
    console.log('登录请求参数:', data);
    const response = await axios.post(`${API_URL}/auth/login`, data);
    console.log('登录响应:', response);
    console.log('登录响应头:', response.headers);
    
    // 从响应头获取JWT
    const authHeader = response.headers['authorization'];
    console.log('Authorization头:', authHeader);
    
    if (authHeader && authHeader.startsWith('Bearer ')) {
      const token = authHeader.substring(7);
      console.log('提取的token:', token);
      
      // 存储用户信息和token
      const userData = {
        user: response.data,
        token: token
      };
      
      console.log('将要存储的用户数据:', userData);
      
      // 根据是否选择"记住我"来决定存储位置
      if (data.rememberMe) {
        // 记住登录状态，使用localStorage（浏览器关闭后仍然保留）
        localStorage.setItem(config.TOKEN_KEY, JSON.stringify(userData));
        console.log('用户数据已存储到localStorage');
      } else {
        // 不记住登录状态，使用sessionStorage（浏览器关闭后自动清除）
        sessionStorage.setItem(config.TOKEN_KEY, JSON.stringify(userData));
        console.log('用户数据已存储到sessionStorage');
      }
      
      // 设置默认请求头
      axios.defaults.headers.common['Authorization'] = `Bearer ${token}`;
      console.log('已设置axios默认Authorization头');
    } else {
      console.warn('未找到Authorization头或格式不正确');
    }
    
    console.log('返回的用户数据:', response.data);
    return response.data;
  } catch (error) {
    console.error('登录错误:', error);
    throw error;
  }
};

// 用户注册
export const register = async (data: RegisterRequest): Promise<any> => {
  try {
    const response = await axios.post(`${API_URL}/auth/register`, data);
    return response.data;
  } catch (error) {
    throw error;
  }
};

// 获取当前用户信息
export const getCurrentUser = async (): Promise<User | null> => {
  try {
    // 先检查sessionStorage（会话存储）
    let userDataStr = sessionStorage.getItem(config.TOKEN_KEY);
    console.log('从sessionStorage获取的用户数据:', userDataStr);
    
    // 如果sessionStorage中没有，再检查localStorage（本地存储）
    if (!userDataStr) {
      userDataStr = localStorage.getItem(config.TOKEN_KEY);
      console.log('从localStorage获取的用户数据:', userDataStr);
    }
    
    // 如果两处都没有找到，则返回null
    if (!userDataStr) {
      console.warn('本地存储中未找到用户数据');
      return null;
    }
    
    const userData = JSON.parse(userDataStr);
    console.log('解析后的用户数据:', userData);
    
    if (!userData || !userData.token) {
      console.warn('用户数据不完整或没有token');
      return null;
    }
    
    // 设置请求头的认证信息
    axios.defaults.headers.common['Authorization'] = `Bearer ${userData.token}`;
    console.log('已设置axios默认Authorization头:', `Bearer ${userData.token}`);
    
    // 如果已有用户信息直接返回
    if (userData.user) {
      console.log('从本地存储返回用户信息:', userData.user);
      return userData.user;
    }
    
    // 否则从服务器获取
    console.log('从服务器获取用户信息...');
    const response = await axios.get(`${API_URL}/auth/me`);
    console.log('服务器返回的用户信息:', response.data);
    return response.data;
  } catch (error) {
    console.error('获取当前用户失败:', error);
    // 如果获取失败，清除本地存储
    localStorage.removeItem(config.TOKEN_KEY);
    sessionStorage.removeItem(config.TOKEN_KEY);
    delete axios.defaults.headers.common['Authorization'];
    console.log('已清除本地存储和Authorization头');
    return null;
  }
};

// 退出登录
export const logout = (): void => {
  localStorage.removeItem(config.TOKEN_KEY);
  sessionStorage.removeItem(config.TOKEN_KEY);
  delete axios.defaults.headers.common['Authorization'];
}; 