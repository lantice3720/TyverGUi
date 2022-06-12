# TyverGUi
튀버서버 GUI 플러그인

주의: 스파게티 코드. 버그 발생할 수 있음.

저작권 관련으로 따질일이 과연 있을지는 모르겠지만, 일단은 본인에게 있는 것으로 함. 이견 있을시 디스코드 lanthanide#7257 로 연락요망

아마도 고쳐지지 않을 것
- guiHashMap iterate 대신 사용 가능한 방안 모색
  - gui 열린 후 displayname 출력시 맨뒤에 name 을 컬러코드로 암호화한 것을 끼워넣는 방법
  - gui 연 플레이어에게 persistantDataContatiner 등을 사용해 정보저장
    - 플러그인 리로드 시 오작동 위험 있을것으로 생각됨
- 현재 열린 gui에서 displayname 으로 gui 받아오는 상태. displayname 는 유니크값이 아니므로 위 방안들을 이용해야 함
- 보다 편리한 gui 제작
  - 상자 내용물 옮겨오기
  - 리로드시 자동저장
  - 클릭 커맨드 쉽게 편집
- 저장 시스템 데이터베이스화
