## auth

`POST` "/api/register"

    :params
        :username
        :password

    :success
        "" -> fail, username existed.
        user -> success, save it.

    :failure
        error-info {
            :status
            :status-text
        } -> show  an modal box to tell the user.

`POST` '/api/login'

    :params
        :username
        :password

    :success
        user -> success
        "" -> not correct

`POST` '/api/logout'

    :success
        (ok "")
    :failure
        (403 no-access)

## posts

`GET`  '/api/posts/:section'
    :return
        [Post] -> success

    :error
        1, no such section
        2, server error
`GET` '/api/post/:id

    :success


## users

`GET` '/api/user/`
    {:id}

    :return
        user -> success
        "" -> no such user
`GET`
