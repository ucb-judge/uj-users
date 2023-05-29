package ucb.judge.ujusers.dao
import javax.persistence.*

@Entity
@Table(name = "campus")
class Campus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "campus_id")
    var campusId: Long = 0;

    @Column(name = "name")
    var name: String = "";

    @Column(name = "status")
    var status: Boolean = true;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "campus")
    var campusMajors: List<CampusMajor>? = null;
}