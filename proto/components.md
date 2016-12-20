##components

### nav-header
subscribe [:current-section]
dispatch [:set-section]

### page-nav
subscribe {
  :page
}
dispatch{
  :set-page
}
### home-page
### about-page

### forum-page
### section-nav
subscribe{
  :current-section
}
dispatch{
  :set-section
}
### section
subscribe {
  :state-of-section [section]
  :new-post
  :current-post
  :posts-in-[section] 
}
dispatch {
  :set-state-of-section [section]
}

> the 3 types of view match the state of current section

#### new-view
subscribe{
  :new-post-in-section [section]
}
dispatch{
  :submit-new-post-in-section [section]
  :cancel-new-post-in-section [section]
}

#### list-view
subscribe{
  :posts-in-section [section]
}

#### single-view

### post-item
dispatch [:set-user, :set-post]

### post-page
subscribe {
  
}
dispatch  {
  : submit-new-comment
}


### sidebar
subscribe [:current-user
        :selected-user]

### 

