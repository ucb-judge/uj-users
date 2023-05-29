package ucb.judge.ujusers.dao

import javax.persistence.*

@Entity
@Table(name = "campus_major")
class CampusMajor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "campus_major_id")
    var campusMajorId: Long = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "campus_id")
    var campus: Campus? = null;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "major_id")
    var major: Major? = null;

    @Column(name = "status")
    var status: Boolean = true;
//
//    @OneToMany(fetch = FetchType.LAZY, mappedBy = "campusMajor")
//    var students: List<Student>? = null;
}