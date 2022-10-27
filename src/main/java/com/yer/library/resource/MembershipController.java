package com.yer.library.resource;

import com.yer.library.model.Membership;
import com.yer.library.model.Response;
import com.yer.library.service.MembershipService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static com.yer.library.resource.Constants.MAX_PAGE_SIZE;
import static com.yer.library.resource.ControllerUtil.getDataMap;
import static java.time.LocalDateTime.now;
import static org.springframework.http.HttpStatus.*;

@RestController
@RequestMapping(path = "api/v1/memberships")
@RequiredArgsConstructor
public class MembershipController {
    private final MembershipService membershipService;

    @GetMapping(path = "{membershipId}")
    public ResponseEntity<Response> getMembership(@PathVariable("membershipId") Long membershipId) {
        return ResponseEntity.ok(
                Response.builder()
                        .timeStamp(now())
                        .data(getDataMap("membership", membershipService.get(membershipId)))
                        .message("Membership " + membershipId + " retrieved")
                        .status(OK)
                        .statusCode(OK.value())
                        .build()
        );
    }

    @GetMapping
    public ResponseEntity<Response> getMemberships() {
        return ResponseEntity.ok(
                Response.builder()
                        .timeStamp(now())
                        .data(getDataMap("memberships", membershipService.list(MAX_PAGE_SIZE)))
                        .message("Memberships retrieved")
                        .status(OK)
                        .statusCode(OK.value())
                        .build()
        );
    }

    @GetMapping(path = "/list_by_type/{membershipTypeId}")
    public ResponseEntity<Response> getMemberships(@PathVariable("membershipTypeId") Long membershipTypeId) {
        return ResponseEntity.ok(
                Response.builder()
                        .timeStamp(now())
                        .data(getDataMap("memberships", membershipService.listByMembershipType(membershipTypeId, MAX_PAGE_SIZE)))
                        .message("Memberships for membership type with ID " + membershipTypeId + " retrieved")
                        .status(OK)
                        .statusCode(OK.value())
                        .build()
        );
    }

    @PostMapping
    public ResponseEntity<Response> addMembership(
            @RequestParam Long membershipTypeId,
            @RequestBody @Valid Membership membership
    ) {
        return ResponseEntity.ok(
                Response.builder()
                        .timeStamp(now())
                        .data(getDataMap("membership", membershipService.add(membership, membershipTypeId)))
                        .message("Membership created")
                        .status(CREATED)
                        .statusCode(CREATED.value())
                        .build()
        );
    }

    @PutMapping(path = "{membershipId}")
    public ResponseEntity<Response> updateMembership(
            @RequestParam Long membershipTypeId,
            @PathVariable("membershipId") Long membershipId,
            @RequestBody @Valid Membership membership
    ) {
        return ResponseEntity.ok(
                Response.builder()
                        .timeStamp(now())
                        .data(getDataMap("membership", membershipService.fullUpdate(membershipId, membership, membershipTypeId)))
                        .message("Membership " + membershipId + " updated")
                        .status(OK)
                        .statusCode(OK.value())
                        .build()
        );
    }

    @DeleteMapping(path = "{membershipId}")
    public ResponseEntity<Response> deleteMembership(@PathVariable("membershipId") Long membershipId) {
        return ResponseEntity.ok(
                Response.builder()
                        .timeStamp(now())
                        .data(getDataMap("delete", membershipService.delete(membershipId)))
                        .message("Membership " + membershipId + " deleted")
                        .status(NO_CONTENT)
                        .statusCode(NO_CONTENT.value())
                        .build()
        );
    }
}
