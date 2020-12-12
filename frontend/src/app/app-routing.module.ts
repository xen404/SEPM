import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {HomeComponent} from './components/home/home.component';
import {LoginComponent} from './components/login/login.component';
import {AuthGuard} from './guards/auth.guard';
import {MessageComponent} from './components/message/message.component';
import {TopEventsComponent} from './components/top-events/top-events.component';
import {UserProfilComponent} from './components/user-profil/user-profil.component';
import {ArtistComponent} from './components/artist/artist.component';
import {LocationComponent} from './components/location/location.component';
import {ArtistDetailComponent} from './components/artist-detail/artist-detail.component';
import {LocationDetailsComponent} from './components/location-details/location-details.component';
import {SignupComponent} from './components/signup/signup.component';
import {ShowDetailComponent} from './components/show-detail/show-detail.component';
import {UserRegisterComponent} from './components/user-register/user-register.component';
import {UserManagementComponent} from './components/user-management/user-management.component';
import { NewsDetailComponent } from './components/news-detail/news-detail.component';
import {NewsComponent} from './components/news/news.component';
import {EventsComponent} from './components/events/events.component';
import {EventDetailsComponent} from './components/event-details/event-details.component';
import {EventCategoryComponent} from './components/event-category/event-category.component';
import {AddEventComponent} from './components/add-event/add-event.component';
import {ShowsComponent} from './components/shows/shows.component';
import {UserBonusComponent} from './components/user-bonus/user-bonus.component';
import {MerchandiseComponent} from './components/merchandise/merchandise.component';
import {MerchandiseBuyComponent} from './components/merchandise-buy/merchandise-buy.component';
import {UserOrdersComponent} from './components/user-orders/user-orders.component';

const routes: Routes = [
  {path: '', component: HomeComponent},
  {path: 'login', component: LoginComponent},
  {path: 'message', canActivate: [AuthGuard], component: MessageComponent},
  {path: 'user-profil', component: UserProfilComponent},
  {path: 'top-events', component: TopEventsComponent},
  {path: 'message', canActivate: [AuthGuard], component: MessageComponent},
  {path: 'news', canActivate: [AuthGuard], component: NewsComponent},
  {path: 'signUp', component: SignupComponent},
  {path: 'artist', component: ArtistComponent},
  {path: 'venues', component: LocationComponent},
  {path: 'artist-detail/:id', component: ArtistDetailComponent},
  {path: 'location-detail/:id', component: LocationDetailsComponent},
  {path: 'show-detail/:id', canActivate: [AuthGuard], component: ShowDetailComponent},
  {path: 'user-bonus', component: UserBonusComponent},
  {path: 'merchandise', component: MerchandiseComponent},
  {path: 'news-detail/:id', canActivate: [AuthGuard], component: NewsDetailComponent},
  {path: 'user-register', canActivate: [AuthGuard], component: UserRegisterComponent},
  {path: 'user-management', canActivate: [AuthGuard], component: UserManagementComponent},
  {path: 'events', component: EventsComponent},
  {path: 'events-detail/:id', component: EventDetailsComponent},
  {path: 'events-category/:category', component: EventCategoryComponent},
  {path: 'add-event', component: AddEventComponent},
  {path: 'events-category/:category/:createdEvent', component: EventCategoryComponent},
  {path: 'shows', component: ShowsComponent},
  {path: 'merchandise-buy/:id', component: MerchandiseBuyComponent},
  {path: 'my-orders', component: UserOrdersComponent}

];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {
}
