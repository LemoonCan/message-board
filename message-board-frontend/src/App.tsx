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
      try {
        // 检查本地存储的用户信息或token
        let userDataStr = sessionStorage.getItem(config.TOKEN_KEY);
        
        if (!userDataStr) {
          userDataStr = localStorage.getItem(config.TOKEN_KEY);
        }
        
        if (userDataStr) {
          try {
            const userData = JSON.parse(userDataStr);
            
            // 如果本地存储中有完整的用户信息，直接使用
            if (userData && userData.user) {
              setUser(userData.user);
            }
            
            // 额外验证token是否有效
            const currentUser = await getCurrentUser();
            
            if (currentUser) {
              setUser(currentUser);
            } else {
              // 如果token无效，清除存储
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
        } 
      } catch (error) {
        console.error('认证初始化错误:', error);
        localStorage.removeItem(config.TOKEN_KEY);
        sessionStorage.removeItem(config.TOKEN_KEY);
        setUser(null);
      } finally {
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