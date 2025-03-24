import React, { useState, useEffect } from 'react';
import { Layout, Card, Form, Input, Button, List, Avatar, Typography, Divider, Spin, message, Dropdown, Menu } from 'antd';
import { Comment } from '@ant-design/compatible';
import { Link } from 'react-router-dom';
import { UserOutlined, CommentOutlined, LogoutOutlined, DownOutlined } from '@ant-design/icons';
import { User, logout } from '../services/auth';
import { Message, getAllMessages, getMessageReplies, createMessage, replyToMessage } from '../services/message';
import MessageItem from '../components/MessageItem';

const { Header, Content, Footer } = Layout;
const { TextArea } = Input;
const { Title, Text } = Typography;

interface HomeProps {
  user: User | null;
}

const Home: React.FC<HomeProps> = ({ user }) => {
  const [messages, setMessages] = useState<Message[]>([]);
  const [loading, setLoading] = useState(false);
  const [submitting, setSubmitting] = useState(false);
  const [expandedMessageIds, setExpandedMessageIds] = useState<number[]>([]);
  const [repliesLoading, setRepliesLoading] = useState<{[key: number]: boolean}>({});
  const [form] = Form.useForm();
  const [initialLoaded, setInitialLoaded] = useState(false); // 标记初始数据是否已加载

  // 检查消息树数据是否有效
  const validateMessageTree = (data: Message[]): boolean => {
    if (!Array.isArray(data)) {
      return false;
    }
    return true;
  };

  // 加载所有留言
  const fetchMessages = async (showLoading = true) => {
    // 初次加载显示loading，后续更新不显示以避免闪烁
    if (showLoading && !initialLoaded) {
      setLoading(true);
    }
    
    try {
      const data = await getAllMessages();
      
      if (validateMessageTree(data)) {
        setMessages(data);
        if (!initialLoaded) {
          setInitialLoaded(true);
        }
      } else {
        message.error('接收到的消息数据格式不正确');
      }
    } catch (error) {
      console.error('Error fetching messages:', error);
      message.error('获取留言失败');
    } finally {
      if (showLoading && !initialLoaded) {
        setLoading(false);
      }
    }
  };

  // 加载留言回复 (备用方法，完整树中已包含回复)
  const fetchReplies = async (messageId: number) => {
    setRepliesLoading(prev => ({ ...prev, [messageId]: true }));
    try {
      const replies = await getMessageReplies(messageId);
      
      setMessages(prevMessages => {
        return prevMessages.map(msg => {
          if (msg.id === messageId) {
            return { ...msg, replies };
          }
          return msg;
        });
      });
    } catch (error) {
      console.error(`Error fetching replies for message ${messageId}:`, error);
      message.error('获取回复失败');
    } finally {
      setRepliesLoading(prev => ({ ...prev, [messageId]: false }));
    }
  };

  // 切换展开/收起回复
  const toggleExpand = (messageId: number) => {
    if (expandedMessageIds.includes(messageId)) {
      setExpandedMessageIds(expandedMessageIds.filter(id => id !== messageId));
    } else {
      setExpandedMessageIds([...expandedMessageIds, messageId]);
    }
  };

  // 提交新留言
  const handleSubmit = async (values: { content: string }) => {
    if (!user) {
      message.error('请先登录');
      return;
    }

    // 检查content是否为undefined或null
    if (!values || values.content === undefined || values.content === null) {
      message.error('留言内容不能为空');
      return;
    }

    if (!values.content.trim()) {
      message.error('留言内容不能为空');
      return;
    }

    setSubmitting(true);
    try {
      await createMessage({ content: values.content });
      message.success('留言发布成功');
      form.resetFields();
      // 重新获取数据但不显示loading状态
      fetchMessages(false);
    } catch (error) {
      console.error('Error submitting message:', error);
      message.error('留言发布失败');
    } finally {
      setSubmitting(false);
    }
  };

  // 提交回复
  const handleReply = async (messageId: number, content: string) => {
    if (!user) {
      message.error('请先登录');
      return;
    }

    if (!content.trim()) {
      message.error('回复内容不能为空');
      return;
    }

    setSubmitting(true);
    try {
      await replyToMessage(messageId, content);
      message.success('回复发布成功');
      // 重新获取数据但不显示loading状态
      fetchMessages(false);
    } catch (error) {
      console.error('Error submitting reply:', error);
      message.error('回复发布失败');
    } finally {
      setSubmitting(false);
    }
  };

  // 处理登出
  const handleLogout = () => {
    logout();
    window.location.reload(); // 刷新页面以清除用户状态
  };

  // 用户菜单
  const userMenu = (
    <Menu>
      <Menu.Item key="logout" icon={<LogoutOutlined />} onClick={handleLogout}>
        退出登录
      </Menu.Item>
    </Menu>
  );

  useEffect(() => {
    fetchMessages();
  }, []);

  // 渲染留言回复表单
  const ReplyForm = ({ messageId }: { messageId: number }) => {
    const [replyContent, setReplyContent] = useState('');
    const [replySubmitting, setReplySubmitting] = useState(false);

    const submitReply = async () => {
      setReplySubmitting(true);
      try {
        await handleReply(messageId, replyContent);
        setReplyContent('');
      } finally {
        setReplySubmitting(false);
      }
    };

    return user ? (
      <div style={{ marginTop: 16, marginBottom: 16 }}>
        <TextArea
          rows={2}
          value={replyContent}
          onChange={e => setReplyContent(e.target.value)}
          placeholder="写下你的回复..."
        />
        <Button
          htmlType="submit"
          loading={replySubmitting}
          onClick={submitReply}
          type="primary"
          style={{ marginTop: 8 }}
        >
          回复
        </Button>
      </div>
    ) : (
      <div style={{ marginTop: 8, marginBottom: 16 }}>
        <Text type="secondary">
          <Link to="/login">登录</Link> 后才能回复
        </Text>
      </div>
    );
  };

  return (
    <Layout style={{ minHeight: '100vh' }}>
      <Header style={{ 
        position: 'fixed', 
        zIndex: 1, 
        width: '100%', 
        display: 'flex', 
        alignItems: 'center',
        justifyContent: 'space-between',
        padding: '0 50px',
        background: '#fff',
        boxShadow: '0 2px 8px rgba(0,0,0,0.06)'
      }}>
        <div style={{ fontWeight: 'bold', fontSize: 20 }}>
          <CommentOutlined /> 留言板
        </div>
        <div>
          {user ? (
            <Dropdown overlay={userMenu} trigger={['click']}>
              <div style={{ display: 'flex', alignItems: 'center', cursor: 'pointer' }}>
                <Avatar style={{ backgroundColor: '#1890ff' }} icon={<UserOutlined />} />
                <span style={{ margin: '0 8px' }}>{user.username}</span>
                <DownOutlined style={{ fontSize: 12 }} />
              </div>
            </Dropdown>
          ) : (
            <div>
              <Link to="/login">
                <Button type="link">登录</Button>
              </Link>
              <Link to="/register">
                <Button type="primary">注册</Button>
              </Link>
            </div>
          )}
        </div>
      </Header>

      <Content style={{ padding: '0 50px', marginTop: 64 }}>
        <div className="container" style={{ marginTop: 32 }}>
          {user && (
            <Card style={{ marginBottom: 24 }}>
              <Title level={4}>发表留言</Title>
              <Form form={form} onFinish={handleSubmit}>
                <Form.Item name="content" 
                rules={[
                  { min: 3, message: '至少3个字符' },
                  { max: 200, message: '最多200个字符'}
                ]}
                >
                  <TextArea rows={4} placeholder="写下你的想法..." />
                </Form.Item>
                <Form.Item>
                  <Button htmlType="submit" loading={submitting} type="primary">
                    发布留言
                  </Button>
                </Form.Item>
              </Form>
            </Card>
          )}

          <Card>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 16 }}>
              <Title level={4} style={{ margin: 0 }}>所有留言</Title>
              <Button onClick={() => fetchMessages(true)} type="default" size="small">
                刷新留言
              </Button>
            </div>
            {loading ? (
              <div style={{ textAlign: 'center', padding: '20px 0' }}>
                <Spin />
              </div>
            ) : (
              <List
                itemLayout="vertical"
                dataSource={messages}
                renderItem={item => (
                  <List.Item 
                    key={item.id}
                    style={{ 
                      borderBottom: '1px solid #f0f0f0', 
                      padding: '16px 0',
                      marginBottom: 0
                    }}
                  >
                    <MessageItem 
                      message={item}
                      user={user}
                      expanded={expandedMessageIds.includes(item.id)}
                      onToggleExpand={toggleExpand}
                      onDataChange={() => fetchMessages(false)} // 传递数据更新回调
                    />
                  </List.Item>
                )}
                locale={{ emptyText: '暂无留言' }}
              />
            )}
          </Card>
        </div>
      </Content>

      <Footer style={{ textAlign: 'center' }}>
        留言板 ©{new Date().getFullYear()} Created with Ant Design
      </Footer>
    </Layout>
  );
};

export default Home; 