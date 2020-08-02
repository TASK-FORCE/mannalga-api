package com.taskforce.superinvention.app.domain

import com.taskforce.superinvention.app.domain.user.UserService
import org.springframework.boot.test.context.SpringBootTest
import spock.lang.Specification

@SpringBootTest
class MockUserServiceTest extends Specification{

    def "kakao token을 받으면 카카오 유저 id를 조회한다." () {
        given:
        def kakaoToken = Kakao"OKooCiUkEpb8LyF3_p7kljqR-img2GrUIoui_Qo9cpcAAAFzqnHVMw"
        def userService = Mock(UserService)

        when:
        def id = userService.getKakaoId(kakaoToken)

        then:
        1
    }
}
