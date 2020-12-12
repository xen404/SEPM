import { Component, OnInit } from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {User} from '../../dtos/user';
import {UserService} from '../../services/user.service';
import {AuthService} from '../../services/auth.service';
import {Router} from '@angular/router';

@Component({
  selector: 'app-user-register',
  templateUrl: './user-register.component.html',
  styleUrls: ['./user-register.component.css']
})
export class UserRegisterComponent implements OnInit {

  registerForm: FormGroup;
  // Error flag
  submitted: boolean = false;
  registered: boolean = false;
  error: boolean = false;
  errorMessage: string = '';
  user: User;

  constructor(private formBuilder: FormBuilder, private userService: UserService) {
    this.registerForm = this.formBuilder.group({
      firstName: ['', [Validators.required]],
      surname: ['', [Validators.required]],
      email: ['', [Validators.required, Validators.pattern('([\\w-\\.]+)@((?:[\\w]+\\.)+)([a-zA-Z]{2,4})')]],
      password: ['', [Validators.required, Validators.minLength(8)]],
      rePassword: ['', [Validators.required]],
      admin: ['']
    }, {validator: this.checkPassword('password', 'rePassword') });
  }

  /**
   * Form validation will start after the method is called, additionally an AuthRequest will be sent
   */
  registerUser() {
    this.submitted = true;
    if (this.registerForm.valid) {
      const signUpRequest: User = new User(
        null,
        this.registerForm.controls.firstName.value,
        this.registerForm.controls.surname.value,
        this.registerForm.controls.email.value,
        this.registerForm.controls.password.value,
        this.registerForm.controls.admin.value,
        true,
        0);
      this.userService.signUpUser(signUpRequest).subscribe(
        (user) => { this.user = user;
          console.log('Successfully signed up user: ' + signUpRequest.email);
          this.registered = true;
        },
        error => {
          this.defaultServiceErrorHandling(error);
        }
      );
      this.clearForm();
    } else {
      console.log('Invalid input');
    }
  }
  /**
   * Validator checks if password and confirmed password match.
   */
  checkPassword(controlName: string, matchingControlName: string) {
    return (formGroup: FormGroup) => {
      const control = formGroup.controls[controlName];
      const matchingControl = formGroup.controls[matchingControlName];
      if (matchingControl.errors && !matchingControl.errors.mustMatch) {
        // return if another validator has already found an error on the matchingControl
        return;
      }
      if (control.value !== matchingControl.value) {
        matchingControl.setErrors({misMatch: true});
      } else {
        matchingControl.setErrors(null);
      }
    };
  }

  /**
   * Error flag will be deactivated, which clears the error message
   */
  vanishError() {
    this.error = false;
  }

  private clearForm() {
    this.registerForm.reset();
    this.submitted = false;
    this.registered = false;
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


  ngOnInit(): void {
  }

}
