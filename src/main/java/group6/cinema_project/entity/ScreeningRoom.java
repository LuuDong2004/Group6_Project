package group6.cinema_project.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Data
@Entity
@Table(name = "ScreeningRoom")
@NoArgsConstructor
public class ScreeningRoom {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "name", nullable = false, unique = true, length = 50, columnDefinition = "NVARCHAR(50)")
    private String name;
    @Column(name = "capacity", nullable = false, length = 10, columnDefinition = "INT")
    private int capacity; // sức chứa

    @Column(name = "description", nullable = false, length = 250, columnDefinition = "NVARCHAR(255)")
    private String description;

    @ManyToOne
    @JoinColumn(name = "branch_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Branch branch;

    @Column(name = "type", length = 20, columnDefinition = "NVARCHAR(20)")
    private String type;

    @Column(name = "status", length = 20, columnDefinition = "NVARCHAR(20)")
    private String status;

    // Thêm các field mới
    @Column(name = "rows", nullable = false)
    private int rows; // số hàng ghế

    @Column(name = "seats_per_row", nullable = false)
    private int seatsPerRow; // số ghế mỗi hàng

}
