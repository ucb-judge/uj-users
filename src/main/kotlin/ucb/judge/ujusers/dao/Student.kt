package ucb.judge.ujusers.dao

import javax.persistence.*

@Entity
@Table(name = "student")
class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "student_id")
    var studentId: Long = 0;

    @Column(name = "kc_uuid")
    var kcUuid: String = "";

    @Column(name = "status")
    var status: Boolean = true;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "campus_major_id", nullable = false)
    var campusMajor: CampusMajor? = null;
}