# 만날까
```
취미 및 동호회 서비스 만날까에서
당신의 관심사를 등록하고 모임에 참여하세요!
```
![1](https://user-images.githubusercontent.com/46917538/115131293-f0e03800-a031-11eb-868c-06ac3cf5f415.png)
![2](https://user-images.githubusercontent.com/46917538/115131294-f178ce80-a031-11eb-861a-dfb0b263f48f.png)<br>
![3](https://user-images.githubusercontent.com/46917538/115131295-f2116500-a031-11eb-9d78-736b2548f7ce.png)
![4](https://user-images.githubusercontent.com/46917538/115131296-f2116500-a031-11eb-93c4-d9cae88088f2.png)


## 라이브 서버
- [mannal.ga](http://mannal.ga)
- [API Document](http://mannal.ga:8080/docs/index.html)

## 주 사용 기술
- Kotlin 1.4
- Spring boot
- MySQL

## 서버 아키텍처
![MANNALGA arch](https://user-images.githubusercontent.com/46917538/115134137-91d9ed80-a048-11eb-997f-2a37b4d30cf8.png)
<br>

## 프로젝트 실행 방법
### MySQL 및 스키마 설정
MySQL 데이터베이스는 프로젝트 내부의 [`src/course/sql/ddl.sql`](https://github.com/TASK-FORCE/mannalga-api/blob/develop/src/course/sql/ddl.sql)을 실행하여 스키마를 만들 수 있습니다.<br>
DB 스키마 설정이 완료되면 [`application.properties`](https://github.com/TASK-FORCE/super-invention/blob/develop/src/main/resources/application.properties) 파일에 설정한 DB의 설정을 입력해주어야 합니다.

### 프로젝트 실행하기
만날까 서비스는 프론트와 백엔드가 분리되어있습니다.<br>
백엔드 서비스인 이 프로젝트를 먼저 실행한 후, 프론트 앱을 실행하면 앱 구동이 완료됩니다.

- [Backend API Server (this repository)](https://github.com/TASK-FORCE/mannalga-api)
- [Frontend repository](https://github.com/TASK-FORCE/mannalga-front-app)

## 프로젝트의 특징
#### Github Action으로 구성한 CI/CD
개발자가 생성한 모든 Pull Requests는 빌드/테스트를 자동으로 수행하여 정상적으로 실행될 수 있는지 자동으로 확인하고 있습니다. 따라서 PR에 대한 코드 리뷰를 진행할 때 이 코드가 잘 동작하는지 여부를 검토하지 않아도 됩니다.<br><br>

만날까 프로젝트는 Github Action으로 배포 파이프라인을 구성하였습니다. 배포를 진행할 때 `.jar` 파일과 컨테이너 환경을 모두 배포할 수 있도록 두 가지 버전의 작업을 만들었습니다. 해당 Action은 [여기](https://github.com/TASK-FORCE/mannalga-api/tree/develop/.github/workflows)에서 확인할 수 있습니다.

#### 코드리뷰 기반 개발
개발 Feature는 코드리뷰를 거치지 않으면 머지하지 않는것을 기본 원칙으로 세웠습니다. 이를 통해 우리는 코드의 품질을 개선하고 팀원들이 작성한 코드의 동작을 이해할 수 있었습니다.

#### 테스트 코드 기반 API 문서
API 문서 없이 프론트엔드 개발자와 스펙에 대해 논의하기는 쉽지 않은 일입니다. API의 사용 전제 조건, 사용 권한, 매개 변수, 반환 값 등을 명확하게 전달해야 했고, 문서화 없이는 관리가 거의 불가능했습니다. 그래서 우리는 Restdocs를 통해 API의 모든 동작을 문서화했습니다. 프론트엔드 개발자는 더 이상 백엔드 개발자에게 이 API가 무엇을 하는지 물어볼 필요가 없습니다.<br><br>

다음 코드를 통해 API 문서생성을 자동화하였습니다.<br>
```
result.andExpect(status().isOk)
    .andDo(
        document("club-withdraw",
            getDocumentRequest(),
            getDocumentResponse(),
            pathParameters(parameterWithName("clubSeq").description("모임 시퀀스.")),
            responseFields(
                *commonResponseField()
            )
        )
    )
```

![image](https://user-images.githubusercontent.com/46917538/114274623-55abf900-9a5a-11eb-9ff4-083d2e8db22e.png)





#### Type-Safe JPQL 및 QueryDSL 적용
```
@Transactional
override fun findMeetingApplicationByUserAndMeetingSeq(clubUserParam: ClubUser, meetingSeq: Long): MeetingApplication {
    return from(meetingApplication)
        .join(meetingApplication.meeting, meeting)
        .join(meetingApplication.clubUser, clubUser)
        .join(meeting.club, club)
        .where(
            clubUser.seq.eq(clubUserParam.seq), meeting.seq.eq(meetingSeq)
        ).fetchOne()
}
```

## 추가 정보
### Database ERD
![diagram](https://user-images.githubusercontent.com/46917538/114275243-c18f6100-9a5c-11eb-92ea-79b3142e9766.png)

### Entity Relationship Diagram
![entityManagerFactory(EntityManagerFactoryBuilder)](https://user-images.githubusercontent.com/46917538/114275292-ee437880-9a5c-11eb-93d1-f577f68f858c.png)

