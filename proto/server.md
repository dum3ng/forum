## server
forum.db

schema:

user{
  _id: string(ObjectId),
  create-at: Date,
  username: string,
  password: string
}

post{
  _id: string(ObjectId),
  create-at: Date,
  update-at: Date,
  author: {
    _id: string(ObjectId),
    name: string
  },
  title: string,
  content: text,
  section: string,
  comments: [Comment]
}

comment{
  _id,
  content: text,
  author: {
    _id: string(ObjectId),
    name: string
  },
  create-at,
  update-at,
}

route "api"

`POST` /user/new 
    create a new user

@params: {username, password}
@return: "success"|"fail"
    (defn create-user
      [username password]
      )
```


`GET` /user/:id
  get a user with id

@params: {_id: string}
@return: {_id: string, username: string}
  (defn get-user
  [id]
  )

`GET` /post/:section
  get all the posts which in section

@params: {section: string}
@return: [{
  _id,
  title,
  content,
  create-at,
  update-at,
  author: {
    _id: string,
    name: string
  },
  comments: [{
    author: {
    _id,
    name,
  },
    create-at,
    update-at,
    content
  }]
}]
  (defn get-posts-in-section
  [section]
  )
















