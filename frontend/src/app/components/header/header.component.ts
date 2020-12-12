import {Component, OnInit} from '@angular/core';
import {AuthService} from '../../services/auth.service';
import {UserService} from '../../services/user.service';

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.scss']
})
export class HeaderComponent implements OnInit {

  userName: string;

  constructor(public authService: AuthService, public userService: UserService) {
  }

  /**
   * Returns true if the authenticated user is an admin
   */
  isAdmin(): boolean {
    return this.authService.getUserRole() === 'ADMIN';
  }

  ngOnInit() {
    if (localStorage.getItem('loggedInUserFirstName') != null) {
      this.userName = localStorage.getItem('loggedInUserFirstName');
    } else {
      if (this.userService.getLoggedInUser().firstName != null) {
        this.userName = this.userService.getLoggedInUser().firstName;
      } else {
        this.userName = 'USERNAME NOT LOADED';
      }
    }
  }

}
