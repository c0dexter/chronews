# Chronews

![N|Solid](https://i.imgur.com/jLbEJXf.png) 

"**Chronews**" app is a kind of news aggregator with options to save interesting news in "favorites", share the news with someone by sharing URL provided by third-party apps and displaying a list of favorites on the widget which allows very fast opening stored news in a browser. The app is not providing entire text of news, only a short and long description with the option "Read more" on the original site.  This application is flexible to a user by providing preferences screen where is possible to set many parameters of displaying news for a specific country, language, phrase or popularity, etc. 


# Screenshots

![N|Solid](https://i.imgur.com/7lhj1lV.jpg?1) ![N|Solid](https://i.imgur.com/mKGFgm6.jpg?1) ![N|Solid](https://i.imgur.com/hNY1IHo.jpg?1) 

![N|Solid](https://i.imgur.com/ZA1UOd9.jpg) ![N|Solid](https://i.imgur.com/5RSgiP0.jpg?2) ![N|Solid](https://i.imgur.com/ssK7a87.jpg?1)

![N|Solid](https://i.imgur.com/HX4UaCO.jpg?3) ![N|Solid](https://i.imgur.com/9BBkm8P.jpg?1) ![N|Solid](https://i.imgur.com/Z5u1juK.jpg?1)  

# Video presentation
[![N|Solid](https://i.imgur.com/WAeVU1r.png)](https://www.youtube.com/watch?v=oGGl-FWufDo&t)


# Documentation of the app
[![N|Solid](https://www.shareicon.net/download/2017/04/11/883725_document.ico)](https://docs.google.com/document/d/1dUKXPDBmgN66GCfa0Oo_0rRxxKvlphHT-_mKxx8dKsA/edit?usp=sharing)

**Click the above icon** to read the documenation of this project. This is a description of an idea of this app and steps how to develop application in a chronology order. New features will be described in V2 of this documentation.

# Kanban board @Trello
[![N|Solid](https://i.imgur.com/OgojzKL.png)](https://trello.com/b/Sgv6SXy7/chronews-android-app)

I created my own a Kanban Board by using Trello to manage tasks and issues. **Click on the above image** to see the the actual version.

# Main features
  - displaying top-headlines of news for some country,
  - the news could be showing only for a specific language,
  - ability to narrow top-headlines to the specific category and a specific phrase declared by a user in settings, i.e.: Top-headlines of "Android" in Poland,
  - displaying categories of news defined by a user, so users are able to read news only from categories which interested them
  - opening a news page in the browser to read the entire article,
  - sharing an article by sending URL directly from the app by choosing some app where a user would like to put an URL,
  - searching for news by populating a search phrase,
  - storing interesting articles by adding them to the "Favourites" list,
  - ability to haring all favorite articles by sending a batch of URLs, 
  - displaying favorite news on the Widget with the info about actual time which elapsed since publishing news,
  - login to the app by using a Google account (in the future user's presets will be loaded after reinstalling the app)


# Under the hood
  - Room DB
  - Retrofit
  - Android Debug Database
  - Butterknife
  - Picasso
  - Widget
  - MVVM
  - LiveData
  - Google SSO
  - Google Firebase (analitics)
  - Constraint & Collapsing layouts
  - Fragments
  - Shared Preferences
  - RecyclerViews
  - Bottom navigation


### Installation form the Android Studio
The application requires API keys and configuration of them. Take a look at the following table:

| Service URL | Info |
| ------ | ------ |
| https://newsapi.org | Create an own free developer account to getting an API key |
| https://firebase.google.com | Create an account and prepare a configuration for analytics for this app |

**NOTICE:** You have to configure the project to handle Goolge SSO login and Analitics.

### Todos

 - Fix all bugs from Kanban Board,
 - Add more features (increase amount of receiving news),
 - Make refactor: remove boilerpalate code, make sure that encapsulation is fine,
 - Move classes to the proper packages,
 - Add RXJava,
 - Add Espresso tests,
 - Add unit tests.

### Known issues
- API's issues with sending a broken text of article long description. Missing letters with polish diacritical signs.
- Limited requests to the API: up to 1000 requests (in the free, developer version).

### Information from the author
> Notice that this is a very beginning version of the app and it's still developing
> so you can see some strange behaviour or some desing issues. 
> If you find some bug, please contact with me, I will try to fixed it as soon as I can.
> Other helpful tips or ideas are warmly welocome, so just let me know :)
> Thank you for visiting my repo and sharing your opinions!


License
----

MIT


**Free Software, Hell Yeah!**

[//]: # (These are reference links used in the body of this note and get stripped out when the markdown processor does its job. There is no need to format nicely because it shouldn't be seen. Thanks SO - http://stackoverflow.com/questions/4823468/store-comments-in-markdown-syntax)


   [dill]: <https://github.com/joemccann/dillinger>
