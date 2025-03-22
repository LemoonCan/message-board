import React, { useState, useEffect } from 'react';
import { Routes, Route, Navigate } from 'react-router-dom';
import { ConfigProvider } from 'antd';
import zhCN from 'antd/lib/locale/zh_CN';
import config from './config';

// 页面组件
import Login from './pages/Login';
import Register from './pages/Register';
import Home from './pages/Home';

// 服务
import { getCurrentUser } from './services/auth';

const App: React.FC = () => {
  const [user, setUser] = useState<any>(null);
  const [loading, setLoading] = useState<boolean>(true);

  useEffect(() => {
    const initAuth = async () => {
      console.log('开始初始化认证...');
      try {
        // 检查本地存储的用户信息或token
        let userDataStr = sessionStorage.getItem(config.TOKEN_KEY);
        console.log('App从sessionStorage获取的用户数据:', userDataStr);
        
        if (!userDataStr) {
          userDataStr = localStorage.getItem(config.TOKEN_KEY);
          console.log('App从localStorage获取的用户数据:', userDataStr);
        }
        
        if (userDataStr) {
          try {
            const userData = JSON.parse(userDataStr);
            console.log('App解析后的用户数据:', userData);
            
            // 如果本地存储中有完整的用户信息，直接使用
            if (userData && userData.user) {
              console.log('从本地存储设置用户:', userData.user);
              setUser(userData.user);
            }
            
            // 额外验证token是否有效
            console.log('验证token有效性...');
            const currentUser = await getCurrentUser();
            console.log('从服务器获取的当前用户:', currentUser);
            
            if (currentUser) {
              console.log('设置从服务器获取的用户:', currentUser);
              setUser(currentUser);
            } else {
              // 如果token无效，清除存储
              console.warn('服务器未返回有效用户，清除本地存储');
              localStorage.removeItem(config.TOKEN_KEY);
              sessionStorage.removeItem(config.TOKEN_KEY);
              setUser(null);
            }
          } catch (e) {
            console.error('解析用户数据出错:', e);
            localStorage.removeItem(config.TOKEN_KEY);
            sessionStorage.removeItem(config.TOKEN_KEY);
            setUser(null);
          }
        } else {
          console.log('本地存储中无用户数据');
        }
      } catch (error) {
        console.error('认证初始化错误:', error);
        localStorage.removeItem(config.TOKEN_KEY);
        sessionStorage.removeItem(config.TOKEN_KEY);
        setUser(null);
      } finally {
        console.log('认证初始化完成，用户状态:', user);
        setLoading(false);
      }
    };

    initAuth();
  }, []);

  return (
    <ConfigProvider locale={zhCN}>
      {!loading && (
        <Routes>
          <Route path="/" element={<Home user={user} />} />
          <Route path="/login" element={user ? <Navigate to="/" /> : <Login setUser={setUser} />} />
          <Route path="/register" element={user ? <Navigate to="/" /> : <Register />} />
          <Route path="*" element={<Navigate to="/" />} />
        </Routes>
      )}
    </ConfigProvider>
  );
};

export default App; 