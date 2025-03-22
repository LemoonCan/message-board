package lemoon.messageboard.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "message_closure")
public class MessageClosure {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ancestor_id", nullable = false)
    private Message ancestor;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "descendant_id", nullable = false)
    private Message descendant;
    
    @Column(nullable = false)
    private Integer depth;
} 