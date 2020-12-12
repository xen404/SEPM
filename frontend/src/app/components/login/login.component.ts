import {Component, Input, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {ActivatedRoute, Router} from '@angular/router';
import {AuthService} from '../../services/auth.service';
import {AuthRequest} from '../../dtos/auth-request';
import {UserService} from '../../services/user.service';
import {User} from '../../dtos/user';


@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})

export class LoginComponent implements OnInit {
  loginForm: FormGroup;
  // After first submission attempt, form validation will start
  submitted: boolean = false;
  // Error flag
  error: boolean = false;
  errorMessage: string = '';

  deleteSuccess: boolean = false;
  successMessage: string = 'Successfully deleted account';

  constructor(private formBuilder: FormBuilder, private authService: AuthService, private userService: UserService,
              private router: Router, private route: ActivatedRoute) {
    this.loginForm = this.formBuilder.group({
      username: ['', [Validators.required]],
      password: ['', [Validators.required, Validators.minLength(8)]]
    });
  }

  /**
   * Form validation will start after the method is called, additionally an AuthRequest will be sent
   */
  loginUser() {
    this.submitted = true;
    if (this.loginForm.valid) {
      const authRequest: AuthRequest = new AuthRequest(this.loginForm.controls.username.value, this.loginForm.controls.password.value);
      this.authenticateUser(authRequest);
    } else {
      console.log('Invalid input');
    }
  }

  /**
   * Send authentication data to the authService. If the authentication was successfully, the user will be forwarded to the message page
   * @param authRequest authentication data from the user login form
   */
  authenticateUser(authRequest: AuthRequest) {
    console.log('Try to authenticate user: ' + authRequest.email);
    this.authService.loginUser(authRequest).subscribe(
      () => {
        console.log('Successfully logged in user: ' + authRequest.email);
        this.userService.getUserByEmail(authRequest.email).subscribe(
          (user: User) => {
            this.userService.setLoggedInUser(user);
            localStorage.setItem('loggedInUserEmail', user.email);
            localStorage.setItem('loggedInUserFirstName', user.firstName);
            localStorage.setItem('loggedInUserSurname', user.surname);
          });

        if (this.authService.getUserRole() === 'ADMIN') {
          this.router.navigate(['/user-management']);
        } else {
          this.router.navigate(['']);
        }
      },
      error => {
          console.log('Could not log in due to:');
          this.defaultServiceErrorHandling(error);
      }
    );
  }

  private defaultServiceErrorHandling(error: any) {
    console.log(error);
    this.error = true;
    if (typeof error.error === 'object') {
      this.errorMessage = error.error.error;
    } else {
      this.errorMessage = error.error;
    }
  }

  /**
   * Error flag will be deactivated, which clears the error message
   */
  vanishError() {
    this.error = false;
  }

  vanishSuccess() {
    this.deleteSuccess = false;
  }

  ngOnInit() {
    const deleteSuccessString = this.route.snapshot.paramMap.get('deleteSuccess');
    if (deleteSuccessString === 'true') {
      this.deleteSuccess = true;
    }
  }

}
