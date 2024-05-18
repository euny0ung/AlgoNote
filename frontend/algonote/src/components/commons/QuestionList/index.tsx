import { useRouter } from 'next/router'
import { SimpleButton } from '../Buttons/Button'
import TierImg from '../Tier'
import styles from './QuestionList.module.scss'

interface QuestionListProps {
  id: number
  title: string
  tier: number
  tags: string[]
  complete: string
  time: string
}

const QuestionList = ({
  id,
  title,
  tier,
  tags,
  complete,
  time,
}: QuestionListProps) => {
  const router = useRouter()
  const handleWriteNote = () => {
    const problemId = id
    router.push({
      pathname: '/writenote',
      query: { id: problemId, title },
    })
  }

  const handleViewNote = () => {
    router.push('/mynote')
  }
  const dictionary: { [key: string]: string } = {
    math_theory: '수학 및 이론',
    graph_theory: '그래프',
    data_structure: '자료구조',
    optimization: '전략 및 최적화',
    implementation: '구현',
    string: '문자열',
    math: '수학',
    arithmetic: '사칙연산',
    primality_test: '소수 판정',
    sieve: '에라토스테네스의 체',
    graphs: '그래프 이론',
    graph_traversal: '그래프 탐색',
    bfs: '너비 우선 탐색',
    dfs: '깊이 우선 탐색',
    shortest_path: '최단 경로',
    mst: '최소 스패닝 트리',
    topological_sorting: '위상 정렬',
    dijkstra: '다익스트라',
    floyd_warshall: '플로이드-워셜',
    tsp: '외판원 순회 문제',
    lca: '최소 공통 조상',
    data_structures: '자료구조',
    segtree: '세그먼트 트리',
    hash_set: '해시를 사용한 집합과 맵',
    prefix_sum: '누적 합',
    stack: '스택',
    queue: '큐',
    hashing: '해싱',
    priority_queue: '우선순위 큐',
    deque: '덱',
    tree_set: '트리를 사용한 집합과 맵',
    dp: '다이나믹 프로그래밍',
    greedy: '그리디 알고리즘',
    divide_and_conquer: '분할 정복',
    binary_search: '이분 탐색',
    parametric_search: '매개 변수 탐색',
    knapsack: '배낭 문제',
    lis: '가장 긴 증가하는 부분수열',
    recursion: '재귀',
    two_pointer: '두 포인터',
    simulation: '시뮬레이션',
    sorting: '정렬',
    bruteforcing: '브루트포스 알고리즘',
    backtracking: '백트래킹',
    bitmask: '비트마스킹',
    sliding_window: '슬라이딩 윈도우',
    manacher: '매내처 알고리즘',
    rabin_karp: '라빈-카프',
    kmp: 'KMP',
    trie: '트라이',
    number_theory: '정수론',
    combinatorics: '조합론',
    geometry: '기하학',
    probability: '확률론',
    regex: '정규 표현식',
    linear_algebra: '선형대수학',
    calculus: '미적분학',
    statistics: '통계학',
    flt: '페르마의 소정리',
    extended_euclidean: '확장 유클리드 호제법',
    pythagoras: '피타고라스 정리',
    physics: '물리학',
    euclidean: '유클리드 호제법',
    bayes: '베이즈 정리',
    discrete_log: '이산 로그',
    discrete_sqrt: '이산 제곱근',
    discrete_kth_root: '이산 k제곱근',
    miller_rabin: '밀러-라빈 소수 판별법',
    gaussian_elimination: '가우스 소거법',
    euler_phi: '오일러 피 함수',
    euler_characteristic: '오일러 지표',
    exponentiation_by_squaring: '분할 정복을 이용한 거듭제곱',
    sqrt_decomposition: '제곱근 분할법',
    arbitrary_precision: '임의 정밀도/큰 수 연산',
    polygon_area: '다각형의 넓이',
    line_intersection: '선분 교차 판정',
    point_in_convex_polygon: '볼록 다각형 내부의 점 판정',
    modular_multiplicative_inverse: '모듈로 곱셈 역원',
    inclusion_and_exclusion: '포함 배제의 원리',
    berlekamp_massey: '벌리 캠프 메시',
    geometry_3d: '3차원 기하학',
    linearity_of_expectation: '기댓값의 선형성',
    rotating_calipers: '회전하는 캘리퍼스',
    randomization: '무작위화',
    pollard_rho: '폴라드 로',
    min_enclosing_circle: '최소 외접원',
    point_in_non_convex_polygon: '오목다각형 내부의 점 판정',
    kitamasa: '키타마사',
    numerical_analysis: '수치 해석',
    crt: '중국인의 나머지 정리',
    voronoi: '보로노이 다이어그램',
    delaunay: '델로네 삼각분할',
    lgv: '린드스트롬-게셀-비엔노 보조정리',
    planar_graph: '평면 그래프',
    generating_function: '생성 함수',
    scc: '강한 연결 요소',
    dag: '방향 비순환 그래프',
    bipartite_graph: '이분 그래프',
    euler_path: '오일러 경로',
    euler_tour_technique: '오일러 경로 테크닉',
    '0_1_bfs': '0-1 너비 우선 탐색',
    permutation_cycle_decomposition: '순열 사이클 분할',
    bipartite_matching: '이분 매칭',
    disjoint_set: '분리 집합',
    '2_sat': '2-sat',
    tree_decomposition: '트리 분할',
    bidirectional_search: '양방향 탐색',
    articulation: '단절점과 단절선',
    palindrome_tree: '회문 트리',
    cactus: '선인장',
    biconnected_component: '이중 연결 요소',
    stable_marriage: '안정 결혼 문제',
    tree_isomorphism: '트리 동형 사상',
    directed_mst: '유향 최소 신장 트리',
    splay_tree: '스플레이 트리',
    trees: '트리',
    offline_queries: '오프라인 쿼리',
    monotone_queue_optimization: '단조 큐를 이용한 최적화',
    link_cut_tree: '링크/컷 트리',
    pst: '퍼시스턴트 세그먼트 트리',
    lazyprop: '느리게 갱신되는 세그먼트 트리',
    rb_tree: '레드-블랙 트리',
    hld: 'heavy-light 분할',
    centroid_decomposition: '센트로이드 분할',
    centroid: '센트로이드',
    sparse_table: '희소 배열',
    coordinate_compression: '값 / 좌표 압축',
    smaller_to_larger: '작은 집합에서 큰 집합으로 합치는 테크닉',
    merge_sort_tree: '머지 소트 트리',
    hall: '홀의 결혼 정리',
    dp_tree: '트리에서의 다이나믹 프로그래밍',
    dp_deque: '덱을 이용한 다이나믹 프로그래밍',
    dp_bitfield: '비트필드를 이용한 다이나믹 프로그래밍',
    dp_connection_profile: '커넥션 프로파일을 이용한 다이나믹 프로그래밍',
    dp_sum_over_subsets: '부분집합의 합 다이나믹 프로그래밍',
    dp_digit: '자릿수를 이용한 다이나믹 프로그래밍',
    deque_trick: '덱을 이용한 구간 최댓값 트릭',
    divide_and_conquer_optimization: '분할 정복을 사용한 최적화',
    pbs: '병렬 이분 탐색',
    game_theory: '게임 이론',
    sprague_grundy: '스프란그-그런디 정리',
    hackenbush: '하켄부시 게임',
    cht: '볼록 껍질을 이용한 최적화',
    slope_trick: '함수 개형을 위한 최적화',
    knuth: '크누스 최적화',
    knuth_x: '크누스 x',
    flow: '최대 유량',
    mcmf: '최소 비용 최대 유량',
    mfmc: '최대 유량 최소 컷 정리',
    general_matching: '일반적인 매칭',
    mitm: '중간에서 만나기',
    ternary_search: '삼분 탐색',
    circulation: '서큘레이션',
    fft: '고속 푸리에 변환',
    half_plane_intersection: '반평면 교집합',
    alien: 'Aliens 트릭',
    sweeping: '스위핑',
    constructive: '해 구성하기',
    ad_hoc: '애드 혹',
    case_work: '많은 조건 분기',
    parsing: '파싱',
    suffix_array: '접미사 배열과 lcp 배열',
    bitset: '비트 집합',
    heuristics: '휴리스틱',
    dancing_links: '춤추는 링크',
    aho_corasick: '아호-코라식',
    precomputation: '런타임 전의 전처리',
  }

  return (
    <div>
      <div className={styles.wrapper}>
        <div className={styles.level}>
          <TierImg tier={tier} />
        </div>
        <div className={styles.questionNumber}>
          <p>{id}</p>
        </div>
        <div className={styles.title}>
          <p>{title}</p>
        </div>
        <div className={styles.type}>
          <p>{tags.map((tag) => ` #${dictionary[tag]}`)}</p>
        </div>
        <div className={styles.summitDate}>
          <p>{time}</p>
        </div>
        <div className={styles.writeNote}>
          {complete === 'NOT_YET' ? (
            <SimpleButton
              text="노트 작성하기"
              onClick={handleWriteNote}
              className="back"
              style={{
                // background: '#ffffff',
                border: '1px solid',
                // color: '#3c87fe',
                fontSize: '0.9rem',
                fontFamily: 'Pretendard',
                width: '6.7rem',
                height: '2.2rem',
                borderRadius: '0.7rem',
                fontWeight: '500',
              }}
            />
          ) : (
            <SimpleButton
              text="노트 보러가기"
              onClick={handleViewNote}
              style={{
                // border: 'none',
                fontSize: '0.9rem',
                fontFamily: 'Pretendard',
                width: '6.7rem',
                height: '2.2rem',
                // borderRadius: '8px',
                fontWeight: '500',
              }}
            />
          )}
        </div>
      </div>
      <hr className={styles.bottomHr} />
    </div>
  )
}

export default QuestionList
