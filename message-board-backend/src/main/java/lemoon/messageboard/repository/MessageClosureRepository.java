package lemoon.messageboard.repository;

import lemoon.messageboard.model.Message;
import lemoon.messageboard.model.MessageClosure;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageClosureRepository extends JpaRepository<MessageClosure, Long> {

    // 找到指定消息的所有祖先
    @Query("SELECT mc.ancestor FROM MessageClosure mc WHERE mc.descendant.id = :messageId ORDER BY mc.depth DESC")
    List<Message> findAncestors(@Param("messageId") Long messageId);
    
    // 找到指定消息的直接父级（深度为1的祖先）
    @Query("SELECT mc.ancestor FROM MessageClosure mc WHERE mc.descendant.id = :messageId AND mc.depth = 1")
    Message findParent(@Param("messageId") Long messageId);
    
    // 找到所有顶级留言（没有父级的留言）
    @Query("SELECT m FROM Message m WHERE NOT EXISTS (SELECT 1 FROM MessageClosure mc WHERE mc.descendant.id = m.id AND mc.depth > 0) order by m.createdAt DESC")
    List<Message> findRootMessages();
    
    // 找到指定消息的所有后代
    @Query("SELECT mc.descendant FROM MessageClosure mc WHERE mc.ancestor.id = :messageId ORDER BY mc.depth ASC")
    List<Message> findDescendants(@Param("messageId") Long messageId);
    
    // 找到指定消息的直接子留言
    @Query("SELECT mc.descendant FROM MessageClosure mc WHERE mc.ancestor.id = :messageId AND mc.depth = 1 ORDER BY mc.descendant.createdAt DESC")
    List<Message> findChildren(@Param("messageId") Long messageId);
    
    // 根据深度获取层级留言
    @Query("SELECT mc.descendant FROM MessageClosure mc WHERE mc.ancestor.id = :rootId AND mc.depth <= :maxDepth ORDER BY mc.depth ASC, mc.descendant.createdAt DESC")
    List<Message> findMessagesByMaxDepth(@Param("rootId") Long rootId, @Param("maxDepth") Integer maxDepth);
    
    // 删除与指定留言相关的所有闭包表记录
    @Modifying
    @Query("DELETE FROM MessageClosure mc WHERE mc.ancestor.id = :messageId OR mc.descendant.id = :messageId")
    void deleteByMessageId(@Param("messageId") Long messageId);
} 