// 环境变量
const ENV = process.env.NODE_ENV || 'development';

// 环境类型
type EnvType = 'development' | 'test' | 'production';

// 确保ENV值是有效的环境类型
const validEnvs: EnvType[] = ['development', 'test', 'production'];
const currentEnv: EnvType = validEnvs.includes(ENV as EnvType) 
  ? (ENV as EnvType) 
  : 'development';

// 不同环境的配置
const configs = {
  development: {
    API_URL: 'http://localhost:8082/api',
  },
  test: {
    API_URL: 'http://test-api.example.com/api',
  },
  production: {
    API_URL: 'https://api.example.com/api',
  },
};

// 根据环境变量选择配置
const config = {
  // 允许通过.env文件或环境变量覆盖配置
  API_URL: process.env.REACT_APP_API_URL || configs[currentEnv].API_URL,
  
  // 其他配置项
  TOKEN_KEY: 'user',
  VERSION: '1.0.0',
};

export default config; 