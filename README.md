# libraryV2
Library Sample Project Backend in Java Spring

## Entities
* Books: a valid book contains the following:
  * a non-empty, valid `isbn`
  * a non-empty `title`
  * an optional release `year`, that, if specified, cannot be in the future
  * an optional `author`
  * a book `type` (can be "fiction", "non-fiction", or "other")
  * a book `genre` (see [this section](#list-of-valid-book-genres) for a comprehensive list of possible genres)
  * a book `value` between 0 and 99999 (corresponding to the cost of the book, between $0 and $999.99)
* Book copies: a valid book copy contains the following:
  * a location, corresponding to the location in the library; a location consists of the following:
    * a `floor` number between 0 and 3
    * a `bookcase` number between 1 and 100
    * a `shelve` number between 1 and 15
* Memberships: a valid membership contains the following:
  * a `startDate` (must be in the present or past)
  * an `endDate` (must be after the start date, but not more than 5 years after)
* Member: a valid member contains the following:
  * a non-empty `name`
  * an optional `homeAddress`
  * a non-empty, unique, valid, `emailAddress`
  * an optional `birthday`, if present, it must be a date in the past  

## Endpoints
The exposed endpoints are the following:
* `/api/books`
  * GET `/api/books` provides a list of available books
  * GET `/api/books/[id]` displays information about the book with ID `id`
  * POST `/api/books` registers a new book; the JSON body must be a valid book
  * PUT `/api/books/[id]` replaces the book with ID `id` with another; the JSON body must be a valid book
  * PATCH `/api/books/[id]` updates a specific attribute of the book with ID `id`; the request body can be specified following the [JSON patch standard](https://jsonpatch.com)
  * DELETE `/api/books/[id]` (soft) deletes the book with ID `id`
* `/api/book_copies`
  * GET `/api/book_copies` provides a list of available book copies
  * GET `/api/book_copies/list_by_book/[book_id]` provides a list of available book copies belonging to a book with ID `book_id`
  * GET `/api/book_copies/[id]` displays information about the book copy with ID `id`
  * POST `/api/book_copies?book_id=[id]` registers a new book_copy for the book with ID `id`; the JSON body must be a valid book copy
  * PUT `/api/book_copies/[id]` replaces the book copy with ID `id` with another; the JSON body must be a valid book copy
  * PATCH `/api/book_copies/[id]` updates a specific attribute of the book copy with ID `id`; the request body can be specified following the [JSON patch standard](https://jsonpatch.com); you may also replace the `/book` by merely specifying a book `id` in the "value" of the JSON patch request
  * DELETE `/api/book_copies/[id]` (soft) deletes the book copy with ID `id`
* `/api/memberships
  * GET `/api/memberships` provides a list of available memberships
  * GET `/api/memberships/list_by_type[membership_type_id]` provides a list of available memberships with the type of ID `membership_type_id`
  * GET `/api/memberships/[id]` displays information about the membership with ID `id`
  * POST `/api/memberships?membership_type_id=[id]` registers a new membership for the membership_type with ID `id`; the JSON body must be a valid membership
  * PUT `/api/memberships/[id]` replaces the membership with ID `id` with another; the JSON body must be a valid membership
  * PATCH `/api/memberships/[id]` updates a specific attribute of the membership with ID `id`; the request body can be specified following the [JSON patch standard](https://jsonpatch.com); you may also replace the `/membershipType` by merely specifying a membership type `id` in the "value" of the JSON patch request
  * DELETE `/api/memberships/[id]` (soft) deletes the membership with ID `id`
* `/api/members
  * GET `/api/members` provides a list of available members
  * GET `/api/members/list_by_membership[membership_id]` provides a list of available members for the membership with ID `membership_id`
  * GET `/api/members/[id]` displays information about the member with ID `id`
  * POST `/api/members?membership_id=[id]` registers a new member for the membership with ID `id`; the JSON body must be a valid member
  * PUT `/api/members/[id]` replaces the member with ID `id` with another; the JSON body must be a valid member
  * PATCH `/api/members/[id]` updates a specific attribute of the member with ID `id`; the request body can be specified following the [JSON patch standard](https://jsonpatch.com); you may also replace the `/membership` by merely specifying a membership `id` in the "value" of the JSON patch request
  * DELETE `/api/members/[id]` (soft) deletes the member with ID `id`

## List of valid book genres
* classic
* contemporary
* realist
* historical
* drama
* mystery
* crime
* action & adventure
* thriller
* romance
* coming-of-age
* horror
* fantasy
* science fiction
* western
* folklore
* children's
* young adult
* comic book
* graphic novel
* poetry
* comedy
* satire
* philosophical
* religious
