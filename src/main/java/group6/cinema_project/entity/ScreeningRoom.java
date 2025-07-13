
package group6.cinema_project.entity;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Data
@Entity
@Table(name = "ScreeningRoom")
@NoArgsConstructor
@Getter
@Setter
public class ScreeningRoom {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "name", nullable = false, unique = true, length = 50, columnDefinition = "NVARCHAR(50)")
    private String name;

    @Column(name = "capacity", nullable = false, length = 10, columnDefinition = "INT")
    private int capacity; // sức chứa

    @Column(name = "description",nullable = false, length = 250, columnDefinition = "NVARCHAR(255)")
    private String description;

    @Column(name = "type",nullable = false,  length = 20, columnDefinition = "VARCHAR(20)")
    private String type;

    @Column(name = "status",nullable = false, length = 20, columnDefinition = "VARCHAR(20)")
    private String status;
    
    @ManyToOne
    @JoinColumn(name = "branch_id") // sửa lại tên cột cho đúng với DB
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Branch branch;
    
    // Thêm các field mới
    @Column(name = "rows", nullable = false)
    private int rows; // số hàng ghế
    
    @Column(name = "seats_per_row", nullable = false)
    private int seatsPerRow; // số ghế mỗi hàng
}
