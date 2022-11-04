package subway.domain;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class StationRepositoryTest {

    @Autowired
    private StationRepository stations;

    @Autowired
    private LineRepository lines;

    @Autowired
    private TestEntityManager manager;

    @Test
    void save() {
        final Station station = new Station("잠실역");
        // save 함수로 리턴받은 값을 사용해야한다.
        final Station actual = stations.save(station);
        assertThat(actual.getId()).isNotNull();
        assertThat(actual.getName()).isEqualTo("잠실역");
    }

    @Test
    void findByName() {
        stations.save(new Station("잠실역"));
        final Station actual = stations.findByName("잠실역");
        assertThat(actual).isNotNull();
    }

    @Test
    void identity() {
        final Station station1 = stations.save(new Station("잠실역"));
        final Station station2 = stations.findByName("잠실역");
        // 자바 객체의 개념이라면 메모리 주소값이 다르기 때문에 false 겠지만, JPA 영속성 컨텍스트 때문에 동일성을 보장해준다.
        assertThat(station1 == station2).isTrue();
    }

    @Test
    void update() {
        final Station station1 = stations.save(new Station("잠실역"));
        // 영속성 컨텍스트의 스냅샷 정보와 달라지기 때문에 update 쿼리를 날린다.
        // readonly = true 로 설정시 스냅샷 정보를 저장하지 않기 때문에 메모리를 아낄 수 있다.
        station1.changeName("몽촌토성역");
        // jpql이 일어나는 순간에 flush 처리를 해주기 때문에 스냡샷 정보를 확인하여 update 해준다.
        final Station station2 = stations.findByName("몽촌토성역");
        assertThat(station2).isNotNull();
    }

    @Test
    void saveWithLine() {
        final Station station = new Station("잠실역");
        // 연관된 entity는 모두 영속상태 여야만 한다.
        station.setLine(lines.save(new Line("2호선")));
        stations.save(station);
        flushAndClear();
    }

    @Test
    void findByNameWithLine() {
        final Station actual = stations.findByName("교대역");
        assertThat(actual).isNotNull();
        assertThat(actual.getLine()).isNotNull();
        assertThat(actual.getLine().getName()).isEqualTo("3호선");
    }

    @Test
    void updateWithLine() {
        final Line line = lines.save(new Line("2호선"));
        final Station station = stations.findByName("교대역");
        station.setLine(line);
        flushAndClear();
    }

    @Test
    void removeLine() {
        final Station station = stations.findByName("교대역");
        station.setLine(null);
        flushAndClear();
    }

    @Test
    void removeLine2() {
        final Line line = lines.findById(1L).get();
        lines.delete(line);
        flushAndClear();
    }

    private void flushAndClear() {
        manager.flush();
        manager.clear();
    }

}
