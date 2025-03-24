import axios from 'axios';
import config from '../config';

// 从配置中获取API URL
const API_URL = config.API_URL;

export interface Message {
  id: number;
  content: string;
  createdAt: string;
  userId: number;
  customerName?: string;     // 将name改为customerName
  parentId: number | null;
  children?: Message[];
}

export interface CreateMessageRequest {
  content: string;
  parentId?: number | null;
}

// 设置认证头部的辅助函数
const setAuthHeader = () => {
  // 先检查sessionStorage
  let userDataStr = sessionStorage.getItem(config.TOKEN_KEY);
  
  // 如果sessionStorage中没有，再检查localStorage
  if (!userDataStr) {
    userDataStr = localStorage.getItem(config.TOKEN_KEY);
  }
  
  if (userDataStr) {
    const userData = JSON.parse(userDataStr);
    if (userData && userData.token) {
      return `Bearer ${userData.token}`;
    }
  }
  
  return null;
};

// 获取所有顶层留言及其完整嵌套回复树
export const getAllMessages = async (): Promise<Message[]> => {
  try {
    const response = await axios.get(`${API_URL}/messages/full-tree`);
    
    // 验证和清理数据
    const cleanData = (messages: any[]): Message[] => {
      if (!Array.isArray(messages)) return [];
      
      return messages
        .filter(msg => msg && typeof msg === 'object' && msg.id) // 过滤无效消息
        .map(msg => {
          // 清理和验证消息对象
          const cleanedMsg: Message = {
            id: Number(msg.id) || 0,
            content: String(msg.content || ''),
            createdAt: String(msg.createdAt || ''),
            userId: Number(msg.userId) || 0,
            // 将name字段映射到customerName
            customerName: msg.customerName || msg.name || '',
            parentId: msg.parentId ? Number(msg.parentId) : null,
          };
          
          // 递归处理子消息
          if (Array.isArray(msg.children) && msg.children.length > 0) {
            cleanedMsg.children = cleanData(msg.children);
          }
          
          return cleanedMsg;
        });
    };
    
    return cleanData(response.data);
  } catch (error) {
    console.error('Failed to fetch messages:', error);
    return [];
  }
};

// 获取留言回复
export const getMessageReplies = async (messageId: number): Promise<Message[]> => {
  try {
    const response = await axios.get(`${API_URL}/messages/${messageId}/replies`);
    return response.data;
  } catch (error) {
    console.error(`Failed to fetch replies for message ${messageId}:`, error);
    return [];
  }
};

// 创建新留言
export const createMessage = async (data: CreateMessageRequest): Promise<Message> => {
  try {
    const authHeader = setAuthHeader();
    if (!authHeader) {
      throw new Error('未登录');
    }
    
    // 获取当前用户信息
    let userDataStr = sessionStorage.getItem(config.TOKEN_KEY) || localStorage.getItem(config.TOKEN_KEY);
    if (!userDataStr) {
      throw new Error('未找到用户信息');
    }
    
    const userData = JSON.parse(userDataStr);
    const currentUser = userData.user;
    
    if (!currentUser || !currentUser.name) {
      throw new Error('用户信息不完整');
    }
    
    // 构建符合后端MessageDTO格式的请求数据
    const messageDTO = {
      content: data.content,
      customerName: currentUser.name,
      // parentId是可选的，在回复留言时使用
      ...(data.parentId ? { parentId: data.parentId } : {})
    };
    
    
    const response = await axios.post(`${API_URL}/messages/createMessage`, messageDTO, {
      headers: {
        'Authorization': authHeader
      }
    });
    
    return response.data;
  } catch (error) {
    console.error('Failed to create message:', error);
    throw error;
  }
};

// 回复留言
export const replyToMessage = async (parentId: number, content: string): Promise<Message> => {
  try {
    const authHeader = setAuthHeader();
    if (!authHeader) {
      throw new Error('未登录');
    }
    
    // 获取当前用户信息
    let userDataStr = sessionStorage.getItem(config.TOKEN_KEY) || localStorage.getItem(config.TOKEN_KEY);
    if (!userDataStr) {
      throw new Error('未找到用户信息');
    }
    
    const userData = JSON.parse(userDataStr);
    const currentUser = userData.user;
    
    if (!currentUser || !currentUser.name) {
      throw new Error('用户信息不完整');
    }
    
    // 构建符合后端MessageDTO格式的请求数据
    const messageDTO = {
      content: content,
      customerName: currentUser.name
    };
    
    
    const response = await axios.post(`${API_URL}/messages/${parentId}/reply`, messageDTO, {
      headers: {
        'Authorization': authHeader
      }
    });
    
    return response.data;
  } catch (error) {
    console.error('Failed to reply to message:', error);
    throw error;
  }
}; 