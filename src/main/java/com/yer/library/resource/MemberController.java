package com.yer.library.resource;

import com.yer.library.model.Member;
import com.yer.library.model.Response;
import com.yer.library.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static com.yer.library.resource.Constants.MAX_PAGE_SIZE;
import static com.yer.library.resource.ControllerUtil.getDataMap;
import static java.time.LocalDateTime.now;
import static org.springframework.http.HttpStatus.*;

@RestController
@RequestMapping(path = "api/v1/members")
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;

    @GetMapping(path = "{memberId}")
    public ResponseEntity<Response> getMember(@PathVariable("memberId") Long memberId) {
        return ResponseEntity.ok(
                Response.builder()
                        .timeStamp(now())
                        .data(getDataMap("member", memberService.get(memberId)))
                        .message("Member " + memberId + " retrieved")
                        .status(OK)
                        .statusCode(OK.value())
                        .build()
        );
    }

    @GetMapping
    public ResponseEntity<Response> getMembers() {
        return ResponseEntity.ok(
                Response.builder()
                        .timeStamp(now())
                        .data(getDataMap("members", memberService.list(MAX_PAGE_SIZE)))
                        .message("Members retrieved")
                        .status(OK)
                        .statusCode(OK.value())
                        .build()
        );
    }

    @GetMapping(path = "/list_by_membership/{membershipId}")
    public ResponseEntity<Response> getMembers(@PathVariable("membershipId") Long membershipId) {
        return ResponseEntity.ok(
                Response.builder()
                        .timeStamp(now())
                        .data(getDataMap("members", memberService.listByMembership(membershipId, MAX_PAGE_SIZE)))
                        .message("Members retrieved")
                        .status(OK)
                        .statusCode(OK.value())
                        .build()
        );
    }

    @PostMapping
    public ResponseEntity<Response> addMember(
            @RequestParam Long membershipId,
            @RequestBody @Valid Member member
    ) {
        return ResponseEntity.ok(
                Response.builder()
                        .timeStamp(now())
                        .data(getDataMap("member", memberService.add(member, membershipId)))
                        .message("Member created")
                        .status(CREATED)
                        .statusCode(CREATED.value())
                        .build()
        );
    }

    @PutMapping(path = "{memberId}")
    public ResponseEntity<Response> updateMember(
            @RequestParam Long membershipId,
            @PathVariable("memberId") Long memberId,
            @RequestBody @Valid Member member
    ) {
        return ResponseEntity.ok(
                Response.builder()
                        .timeStamp(now())
                        .data(getDataMap("member", memberService.fullUpdate(memberId, member, membershipId)))
                        .message("Member " + memberId + " updated")
                        .status(OK)
                        .statusCode(OK.value())
                        .build()
        );
    }

    @DeleteMapping(path = "{memberId}")
    public ResponseEntity<Response> deleteMember(@PathVariable("memberId") Long memberId) {
        return ResponseEntity.ok(
                Response.builder()
                        .timeStamp(now())
                        .data(getDataMap("soft deleted", memberService.delete(memberId)))
                        .message("Member " + memberId + " deleted")
                        .status(NO_CONTENT)
                        .statusCode(NO_CONTENT.value())
                        .build()
        );
    }
}
