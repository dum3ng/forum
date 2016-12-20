## client side

### root store
{
  page: [:home, :planets, :about] ,
  user: {
    _id,
    username
  } | nil,
  current-section: string ["mars", "earth", "venus", ],
  state-of-section: {
    "mars": "new" | "single" | "list",
    "earth": "new" | "single" | "list",
    "venus": "new" | "single" | "list",
  }

  posts-in-earth: [Post],
  posts-in-mars: [Post],
  posts-in-venus: [Post],
  current-post: Post,
  
  fetching-user: bool,
  fetching-posts-earth: bool,
  fetching-posts-mars: bool,
  fetching-posts-venus: bool,


  submitting-new-post: bool,
  submitting-new-comment: bool,

  post-in-editing: Post | nil,
  comment-in-editing: Comment | nil,
}

### event
:set-section
:set-post
:set-user

<!-- :new-post-in-section -->
:set-state-of-section [section state]

:submit-new-post
:submit-new-post-start
:submitting-new-post
:submit-new-post-success
:submit-new-post-fail

:new-comment
:submit-new-comment
:submit-new-comment-start

:submit-new-comment-success
:submit-new-comment-fail


:fetch-user
:fetch-user-start

:fetch-user-end

:fetch-posts-in-section
:fetch-posts-start

:fetch-posts-end

:
### subs
:page
:section
:user

:state-of-section [section]
:posts-in-section [section]

:submitting-new-post-in-section [section]
:submitting-new-comment-on-post [post]










