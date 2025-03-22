import React, { useState } from 'react';
import { Avatar, List, Button, Input, Spin, Typography } from 'antd';
import { Comment } from '@ant-design/compatible';
import { UserOutlined } from '@ant-design/icons';
import { Link } from 'react-router-dom';
import { Message, getMessageReplies, createMessage, replyToMessage } from '../services/message';
import { User } from '../services/auth';
import { message as antdMessage } from 'antd';

const { TextArea } = Input;
const { Text } = Typography;

interface MessageItemProps {
  message: Message;
  user: User | null;
  expanded: boolean;
  onToggleExpand: (messageId: number) => void;
  level?: number;
  onDataChange?: () => void;
}

// 扩展Message类型以支持内部字段
interface EnhancedMessage extends Message {
  _index?: number;
  _isLast?: boolean;
}

const MessageItem: React.FC<MessageItemProps> = ({
  message,
  user,
  expanded,
  onToggleExpand,
  level = 0,
  onDataChange,
}) => {
  const [replyContent, setReplyContent] = useState('');
  const [submitting, setSubmitting] = useState(false);
  const [loadingReplies, setLoadingReplies] = useState(false);
  const [expandedReplies, setExpandedReplies] = useState<number[]>([]);
  const [showReplyInput, setShowReplyInput] = useState(false);

  const toggleReplyExpand = (replyId: number) => {
    if (expandedReplies.includes(replyId)) {
      setExpandedReplies(expandedReplies.filter(id => id !== replyId));
      // 如果收起了回复，也隐藏输入框
      setShowReplyInput(false);
    } else {
      setExpandedReplies([...expandedReplies, replyId]);
    }
  };

  // 切换回复输入框显示状态
  const toggleReplyInput = (e: React.MouseEvent) => {
    e.stopPropagation(); // 阻止事件冒泡
    setShowReplyInput(!showReplyInput);
  };

  const handleReply = async () => {
    if (!user) {
      antdMessage.error('请先登录');
      return;
    }

    // 检查replyContent是否为undefined或null
    if (replyContent === undefined || replyContent === null) {
      return;
    }

    if (!replyContent.trim()) {
      antdMessage.error('回复内容不能为空');
      return;
    }

    setSubmitting(true);
    try {
      await replyToMessage(message.id, replyContent);
      antdMessage.success('回复发布成功');
      
      // 清空输入框并隐藏
      setReplyContent('');
      setShowReplyInput(false);
      
      // 通知父组件数据已更改，需要刷新
      if (onDataChange) {
        onDataChange();
      }
    } catch (error) {
      console.error('Failed to submit reply:', error);
      antdMessage.error('回复发布失败');
    } finally {
      setSubmitting(false);
    }
  };

  const handleToggleExpand = () => {
    onToggleExpand(message.id);
    // 如果收起了顶层留言，也隐藏输入框
    if (expanded) {
      setShowReplyInput(false);
    }
  };

  // 限制嵌套层级的最大深度
  const MAX_LEVEL = 10;
  const showNestedReplies = level < MAX_LEVEL;
  const hasChildren = message.children && message.children.length > 0;

  // 处理回复列表数据，确保所有数据都是有效的
  const getValidChildren = (): EnhancedMessage[] => {
    if (!message.children || !Array.isArray(message.children)) {
      return [];
    }
    return message.children
      .filter(child => child && typeof child === 'object' && child.id)
      .map((child, index) => {
        // 确保所有必要字段都存在，如果不存在则提供默认值
        const validChild = {
          ...child,
          content: child.content || '',
          customerName: child.customerName || '用户',  // 只使用customerName字段
          createdAt: child.createdAt || '',
          _index: index,
          _isLast: index === message.children!.length - 1
        };
        
        // 处理嵌套children，避免undefined
        if (!validChild.children) {
          validChild.children = [];
        }
        
        return validChild;
      });
  };

  const validChildren = getValidChildren();

  // 格式化显示回复数量
  const formatReplyCount = (count: number) => {
    if (count <= 0) return '';
    return `(${count})`;
  };

  // 格式化日期显示
  const formatDateTime = (dateStr: string | undefined | null) => {
    if (!dateStr) {
      return '';
    }
    
    try {
      const date = new Date(dateStr);
      if (isNaN(date.getTime())) {
        return '';
      }
      return date.toLocaleString();
    } catch (error) {
      return '';
    }
  };

  // 获取用户名，使用customerName字段
  const getUserName = () => {
    return message.customerName || '用户';
  };

  // 渲染回复输入框
  const renderReplyInput = () => {
    if (!showReplyInput) return null;
    
    return (
      <div style={{ marginBottom: 16 }}>
        {user ? (
          <>
            <TextArea
              rows={2}
              value={replyContent}
              onChange={e => setReplyContent(e.target.value)}
              placeholder="写下你的回复..."
            />
            <div style={{ marginTop: 8, display: 'flex', justifyContent: 'flex-end' }}>
              <Button 
                onClick={() => setShowReplyInput(false)}
                style={{ marginRight: 8 }}
              >
                取消
              </Button>
              <Button 
                type="primary" 
                loading={submitting}
                onClick={handleReply}
              >
                回复
              </Button>
            </div>
          </>
        ) : (
          <Text type="secondary">
            <Link to="/login">登录</Link> 后才能回复
          </Text>
        )}
      </div>
    );
  };

  // 渲染子留言列表
  const renderChildrenList = () => {
    if (!hasChildren) {
      return (
        <div style={{ marginBottom: 16 }}>
          <Text type="secondary">暂无回复</Text>
        </div>
      );
    }
    
    return (
      <div style={{ borderLeft: '2px solid #f0f0f0', paddingLeft: 16 }}>
        <List
          dataSource={validChildren}
          locale={{ emptyText: '暂无回复' }}
          renderItem={reply => (
            <List.Item 
              key={reply.id}
              style={{
                borderBottom: reply._isLast ? 'none' : '1px solid #f0f0f0',
                paddingBottom: reply._isLast ? 0 : 8
              }}
            >
              <MessageItem
                message={reply}
                user={user}
                expanded={expandedReplies.includes(reply.id)}
                onToggleExpand={toggleReplyExpand}
                level={level + 1}
                onDataChange={onDataChange}
              />
            </List.Item>
          )}
        />
      </div>
    );
  };

  return (
    <div style={{ display: 'flex', marginBottom: level === 0 ? 16 : 8 }}>
      <div style={{ marginRight: 12 }}>
        <Avatar style={{ backgroundColor: '#1890ff' }} icon={<UserOutlined />} />
      </div>
      <div style={{ flex: 1 }}>
        <div style={{ marginBottom: 4, display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
          <div>
            <Text strong>{getUserName()}</Text>
            {message.createdAt && (
              <Text type="secondary" style={{ marginLeft: 8, fontSize: 12 }}>
                {formatDateTime(message.createdAt)}
              </Text>
            )}
          </div>
        </div>
        <div style={{ marginBottom: 8 }}>
          <p>{message.content}</p>
        </div>
        <div>
          {level === 0 && hasChildren ? (
            <span style={{ display: 'flex', alignItems: 'center' }}>
              <span 
                onClick={handleToggleExpand}
                style={{ cursor: 'pointer', color: '#1890ff', marginRight: 16 }}
              >
                {expanded ? '收起回复' : `查看回复${formatReplyCount(message.children?.length || 0)}`}
              </span>
              {user && (
                <Button 
                  type="link" 
                  size="small"
                  onClick={toggleReplyInput}
                  style={{ padding: '0 4px' }}
                >
                  回复
                </Button>
              )}
            </span>
          ) : level > 0 && hasChildren ? (
            <span style={{ display: 'flex', alignItems: 'center' }}>
              <span 
                onClick={() => toggleReplyExpand(message.id)}
                style={{ cursor: 'pointer', color: '#1890ff', marginRight: 16 }}
              >
                {expandedReplies.includes(message.id) ? '收起回复' : `查看回复${formatReplyCount(message.children?.length || 0)}`}
              </span>
              {user && (
                <Button 
                  type="link" 
                  size="small"
                  onClick={toggleReplyInput}
                  style={{ padding: '0 4px' }}
                >
                  回复
                </Button>
              )}
            </span>
          ) : user ? (
            <Button 
              type="link" 
              size="small"
              onClick={toggleReplyInput}
              style={{ padding: '0 4px' }}
            >
              回复
            </Button>
          ) : null}
        </div>
        
        {renderReplyInput()}
        
        {/* 顶层留言展开内容 */}
        {expanded && level === 0 && (
          <div style={{ marginTop: 16, marginLeft: 8 }}>
            {loadingReplies ? (
              <div style={{ textAlign: 'center', margin: '20px 0' }}>
                <Spin size="small" />
              </div>
            ) : renderChildrenList()}
          </div>
        )}

        {/* 嵌套留言展开内容 */}
        {level > 0 && expandedReplies.includes(message.id) && showNestedReplies && (
          <div style={{ marginTop: 8, borderLeft: '2px solid #f0f0f0', paddingLeft: 16 }}>
            {hasChildren && renderChildrenList()}
          </div>
        )}
      </div>
    </div>
  );
};

export default MessageItem; 