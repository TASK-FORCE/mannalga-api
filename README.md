# SUPER INVENTION

## Live Server
- [mannal.ga](http://mannal.ga)
- [API Document](http://mannal.ga:8080/docs/index.html)

## Main Stack
Project is created with:
- Kotlin 1.4
- Spring boot
- MySQL

## Installation and Getting Started
### Setting MySQL DB And Schema
The MySQL database can create a schema by running [`src/course/sql/ddl.sql`](https://github.com/TASK-FORCE/super-invention/blob/develop/src/course/sql/ddl.sql) attached inside the project.<br>
If the DB schema setting is complete, enter the settings of the DB you set in the [`application.properties`](https://github.com/TASK-FORCE/super-invention/blob/develop/src/main/resources/application.properties) file.

### Start Service
Super Invention software has backend and frontend separation.<br>
You can run the service by running the Front client after you run the Backend API server.


- [Backend API Server (this repository)](https://github.com/TASK-FORCE/super-invention)
- [Frontend repository](https://github.com/TASK-FORCE/super-front)

## Project Features
#### CI/CD with Github Action

#### Publishing with AWS

#### Code-Review Driven Development
Development features will not be long without code review.

#### Test code based API Document
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
#### Type-Safe JPQL via using QueryDSL
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
