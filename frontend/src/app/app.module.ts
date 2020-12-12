import {BrowserModule} from '@angular/platform-browser';
import {NgModule} from '@angular/core';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {HttpClientModule} from '@angular/common/http';

import {AppRoutingModule} from './app-routing.module';
import {AppComponent} from './app.component';
import {HeaderComponent} from './components/header/header.component';
import {FooterComponent} from './components/footer/footer.component';
import {HomeComponent} from './components/home/home.component';
import {LoginComponent} from './components/login/login.component';
import {MessageComponent} from './components/message/message.component';
import {NewsComponent} from './components/news/news.component';
import {ArtistComponent} from './components/artist/artist.component';
import {LocationComponent} from './components/location/location.component';
import {NgbModule} from '@ng-bootstrap/ng-bootstrap';
import {httpInterceptorProviders} from './interceptors';
import { TopEventsComponent } from './components/top-events/top-events.component';
import { UserProfilComponent } from './components/user-profil/user-profil.component';
import { ArtistDetailComponent } from './components/artist-detail/artist-detail.component';
import { LocationDetailsComponent } from './components/location-details/location-details.component';
import { SignupComponent } from './components/signup/signup.component';
import { HeaderAdminComponent } from './components/header-admin/header-admin.component';
import { ShowSeatsComponent } from './components/show-seats/show-seats.component';
import { ShowDetailComponent } from './components/show-detail/show-detail.component';
import { UserBonusComponent } from './components/user-bonus/user-bonus.component';
import { MerchandiseComponent } from './components/merchandise/merchandise.component';
import { MerchandiseItemComponent } from './components/merchandise-item/merchandise-item.component';
import { MerchandiseDetailComponent } from './components/merchandise-detail/merchandise-detail.component';
import { NewsDetailComponent } from './components/news-detail/news-detail.component';
import { UserRegisterComponent } from './components/user-register/user-register.component';
import { UserManagementComponent } from './components/user-management/user-management.component';
import { EventsComponent } from './components/events/events.component';
import { EventDetailsComponent } from './components/event-details/event-details.component';
import { EventCategoryComponent } from './components/event-category/event-category.component';
import { AddShowComponent } from './components/add-show/add-show.component';
import { ShowsComponent } from './components/shows/shows.component';
import {AddEventComponent} from './components/add-event/add-event.component';
import { MerchandiseBuyComponent } from './components/merchandise-buy/merchandise-buy.component';
import { UserOrdersComponent } from './components/user-orders/user-orders.component';
import { SingleOrderComponent } from './components/single-order/single-order.component';

@NgModule({
  declarations: [
    AppComponent,
    HeaderComponent,
    HeaderAdminComponent,
    FooterComponent,
    HomeComponent,
    LoginComponent,
    MessageComponent,
    NewsComponent,
    TopEventsComponent,
    UserProfilComponent,
    ArtistComponent,
    SignupComponent,
    LocationComponent,
    ArtistDetailComponent,
    LocationDetailsComponent,
    ShowSeatsComponent,
    ShowDetailComponent,
    ShowDetailComponent,
    UserBonusComponent,
    MerchandiseComponent,
    MerchandiseItemComponent,
    MerchandiseDetailComponent,
    UserRegisterComponent,
    UserManagementComponent,
    NewsDetailComponent,
    EventsComponent,
    EventDetailsComponent,
    EventCategoryComponent,
    AddShowComponent,
    ShowsComponent,
    AddEventComponent,
    MerchandiseBuyComponent,
    AddEventComponent,
    UserOrdersComponent,
    SingleOrderComponent,
    // AddArtistComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    ReactiveFormsModule,
    HttpClientModule,
    NgbModule,
    FormsModule
  ],
  providers: [httpInterceptorProviders],
  bootstrap: [AppComponent]
})
export class AppModule {
}
