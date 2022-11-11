# libraryV2
Library Sample Project Backend in Java Spring

## General Info
This project is based off of a project for an introductory Java course, in which we built a simple library application using basic Java.

The basic premise of the project was to build an application that keeps track of the books and members of a library. Books can be added, and removed, multiple copies can exist of each book. Members can have a library membership, with which they can reserve books, or loan book copies. This membership can be individual, or a family membership. Loans can be extended, and a fine should be given if the book is handed in too late or not at all, depending on the value of the book.
This application is only accessible to library employees, who can fully interact with the API to add/remove books, members, memberships, and manage reservations and loans. There might also be some general statistics available to view about the library.

Two of this project's participants started again from scratch, but now using the Java Spring Boot framework for the back-end (this repository), and Angular for the frontend (see [this repo](https://github.com/SPersim/libraryApp.git)).

Not all the features of this project have been completed. For now it is possible to add, remove, update, and delete books, book copies, members, and memberships. Loans and reservations have not been implemented.

## Prerequisites
### Java 8
The minimum required Java version is Java 8 ([download](https://www.java.com/en/download/manual.jsp))

### MySQL
This application uses a MySQL database. Make sure to create an empty schema named `libary`, run MySQL on the port `3306`, and create a user `testuser` with password `testpassword` that has Object and DDL rights.
If you wish to change the schema name, port, or user username and password, you can do so in the `src/main/resources/application.properties` file.

### Maven
Make sure Maven is [downloaded](https://maven.apache.org/download.cgi) and [installed](https://maven.apache.org/install.html).

## Installation
To install this application, download a zip file of this repository, and extract the files to a directory of your liking.

## Usage
To run the application, simply run the command `mvn spring-boot:run` from the base directory (`libraryV2-master`)

Now you should be able to interact with all the [endpoints](#Endpoints) via `localhost:8080/[endpoint]`
There is form-based authentication, thus in order to access the endpoints, you would need a username and password.
A valid username/password combination you can use is:
username: `testuser`
password: `testpassword@`

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
* `/api/memberships`
  * GET `/api/memberships` provides a list of available memberships
  * GET `/api/memberships/list_by_type[membership_type_id]` provides a list of available memberships with the type of ID `membership_type_id`
  * GET `/api/memberships/[id]` displays information about the membership with ID `id`
  * POST `/api/memberships?membership_type_id=[id]` registers a new membership for the membership_type with ID `id`; the JSON body must be a valid membership
  * PUT `/api/memberships/[id]` replaces the membership with ID `id` with another; the JSON body must be a valid membership
  * PATCH `/api/memberships/[id]` updates a specific attribute of the membership with ID `id`; the request body can be specified following the [JSON patch standard](https://jsonpatch.com); you may also replace the `/membershipType` by merely specifying a membership type `id` in the "value" of the JSON patch request
  * DELETE `/api/memberships/[id]` (soft) deletes the membership with ID `id`
* `/api/members`
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
