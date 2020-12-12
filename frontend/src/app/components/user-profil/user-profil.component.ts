import {Component, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {AuthService} from '../../services/auth.service';
import {Router} from '@angular/router';
import {User} from '../../dtos/user';
import {UserService} from '../../services/user.service';
import {AuthRequest} from '../../dtos/auth-request';



@Component({
  selector: 'app-user-profil',
  templateUrl: './user-profil.component.html',
  styleUrls: ['./user-profil.component.css']
})
export class UserProfilComponent implements OnInit {


  submitted: boolean = false;
  editPassword: boolean = false;
  passwordEdited: boolean = false;

  error: boolean = false;
  errorMessage: string = '';
  success: boolean = false;
  successMessage: string = 'You have successfully saved your profile changes.';

  originalUser: User;

  editProfileForm = this.formBuilder.group({
    firstName: [localStorage.getItem('loggedInUserFirstName'), [Validators.required]],
    lastName: [localStorage.getItem('loggedInUserSurname'), [Validators.required]],
    mailAddress: [localStorage.getItem('loggedInUserEmail'), [Validators.required, Validators.pattern('([\\w-\\.]+)@((?:[\\w]+\\.)+)([a-zA-Z]{2,4})')]],
  });

    editPasswordForm = this.formBuilder.group({
      currentPassword: ['', [Validators.required]],
      newPassword: ['', [Validators.required, Validators.minLength(8)]],
      newPasswordRepeated: ['', [Validators.required] ]
  },  {validator: this.checkPassword('newPassword', 'newPasswordRepeated') });

  constructor(private formBuilder: FormBuilder, private authService: AuthService,
              private router: Router, private userService: UserService) {}

  async ngOnInit(): Promise<void> {
    this.originalUser = await this.userService.getLoggedInUser();
  }

  /**
   * If form is filled in correctly creates an user according to the form input.
   */
  saveChanges(): void {
    this.vanishAlert();
    this.submitted = true;
    if (this.editPassword === true) {
      this.passwordEdited = true;
      if (this.editPasswordForm.valid) {
        const authRequest: AuthRequest = new AuthRequest(this.originalUser.email, this.editPasswordForm.controls.currentPassword.value);
        this.authenticateUser(authRequest);
      } else {
        console.log('edit password form not valid');
      }
      } else {
      if (this.editProfileForm.valid) {
        const editedUserToBeStored = new User(
          this.originalUser.id,
          this.editProfileForm.controls.firstName.value,
          this.editProfileForm.controls.lastName.value,
          this.editProfileForm.controls.mailAddress.value,
          null,
          this.originalUser.admin,
          this.originalUser.access,
          this.originalUser.bonusPoints);
        this.sendUpdateRequest(editedUserToBeStored);
      } else {
        console.log('Invalid input');
      }
    }
  }

  /**
   * Sends an update request with the updated user profile information. If the request is successful,
   * the updated information of the user is stored in the local storage.
   * @param userToBeStored contains all the information necessary for the request.
   */
  sendUpdateRequest(userToBeStored: User): void {
    this.userService.updateProfile(userToBeStored).subscribe((user: User) => {
      this.userService.setLoggedInUser(user);
      localStorage.setItem('loggedInUserEmail', user.email);
      localStorage.setItem('loggedInUserFirstName', user.firstName);
      localStorage.setItem('loggedInUserSurname', user.surname);
      this.success = true;
      this.successMessage = 'Successfully updated profile details';
      this.cancelPasswordEdit();
    }, error1 => {
      this.defaultServiceErrorHandling(error1);
    });
  }



  /**
   * Send authentication data to the authService to check if user is allowed to change password
   * @param authRequest authentication data from the form
   */
  authenticateUser(authRequest: AuthRequest) {
    this.authService.loginUser(authRequest).subscribe(
      () => {
        const editedUserToBeStored = new User(
          this.originalUser.id,
          this.editProfileForm.controls.firstName.value,
          this.editProfileForm.controls.lastName.value,
          this.editProfileForm.controls.mailAddress.value,
          this.editPasswordForm.controls.newPassword.value,
          this.originalUser.admin,
          this.originalUser.access,
          this.originalUser.bonusPoints);
        this.sendUpdateRequest(editedUserToBeStored);
        this.passwordEdited = false;
      },
      error => {
        console.log('Could not log in due to:');
        this.defaultServiceErrorHandling(error);
      }
    );
  }

  changePassword(): void {
    this.editPassword = true;
  }

  cancelPasswordEdit(): void {
    this.editPassword = false;
    this.editProfileForm.controls.currentPassword.reset();
    this.editPasswordForm.controls.newPassword.reset();
    this.editPasswordForm.controls.newPasswordRepeated.reset();
  }

  /**
   * Sends a delete request to delete the current logged in user.
   * If the delete request is successful, it navigates to the login page.
   */
  deleteAccount(): void {
    this.vanishAlert();
    this.userService.deleteAccount(this.originalUser.id).subscribe( data => {
      this.deleteAccountSuccessful();
    }, error1 => {
      this.defaultServiceErrorHandling(error1);
    });
  }

  /**
   * Logs out user that has been deleted. Navigates to the login page.
   */
  deleteAccountSuccessful(): void {
    this.authService.logoutUser();
    this.router.navigate(['/login', {deleteSuccess: 'true'}]);
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

  private defaultServiceErrorHandling(error: any) {
    console.log(error);
    this.error = true;
    if (typeof error.error === 'object') {
      this.errorMessage = error.error.error;
    } else {
      this.errorMessage = error.error;
    }
  }

  vanishAlert() {
    this.error = false;
    this.success = false;
  }
}
