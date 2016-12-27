## auth
### register

`POST` "/api/register"
  {:username 
  :password}

  :success  "" -> fail, username existed.
          user -> success, save it.
  :failure  error-info {
    :status
    :status-text
  } -> show  an modal box to tell the user.

`POST` '/api/login'
  {:username
  :password}



  `POST` '/api/logout'
