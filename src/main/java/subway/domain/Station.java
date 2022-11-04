package subway.domain;

import org.springframework.data.domain.Persistable;

import javax.persistence.*;

@Entity
public class Station implements Persistable<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;    // 보통 Wrapper 클래스를 사용함

    @Column(nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name = "line_id")
    private Line line;

    protected Station() {}

    public Station(String name) {
        this.name = name;
    }

    public Station(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    // save 할때 select 쿼리를 방지
    public boolean isNew() {
        return true;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Line getLine() {
        return line;
    }

    public void changeName(String name) {
        this.name = name;
    }

    public void setLine(Line line) {
        this.line = line;
        if (!line.getStations().contains(this)) {
            line.getStations().add(this);
        }
    }
}
