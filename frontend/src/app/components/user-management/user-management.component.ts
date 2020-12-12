import {ChangeDetectorRef, Component, OnInit} from '@angular/core';
import {User} from '../../dtos/user';
import {UserService} from '../../services/user.service';
import {NgbPaginationConfig} from '@ng-bootstrap/ng-bootstrap';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {AuthService} from '../../services/auth.service';
import {newArray} from '@angular/compiler/src/util';

@Component({
  selector: 'app-user-management',
  templateUrl: './user-management.component.html',
  styleUrls: ['./user-management.component.css']
})
export class UserManagementComponent implements OnInit {

  passwordForm: FormGroup;
  error: boolean = false;
  errorMessage: string = '';
  private users: User[] = [];
  private editPassword: boolean[];
  private editStatus: boolean[];
  changed: boolean[];
  submitted: boolean[];
  searched: boolean = false;
  numberOfUsers: number = 0;
  page: number = 0;
  pages: number[];
  pageSize: number = 30;
  paginationLimitUp: number = 15;
  paginationLimitDown: number = 0;
  allUsers: number = 0;
  foundUsers: number = 0;


  constructor(private userService: UserService, private ngbPaginationConfig: NgbPaginationConfig, private formBuilder: FormBuilder,
              private cd: ChangeDetectorRef, private authService: AuthService) {
    this.passwordForm = this.formBuilder.group({
      password: ['', [Validators.required, Validators.minLength(8)]],
      rePassword: ['', [Validators.required]]
    }, {validator: this.checkPassword('password', 'rePassword') });
  }

  ngOnInit(): void {
    this.loadUsers();
    this.editStatus = newArray(this.numberOfUsers);
    this.editPassword = newArray(this.numberOfUsers);
    this.submitted = newArray(this.numberOfUsers);
    this.changed = newArray(this.numberOfUsers);
  }

  /**
   * Loads all users from the backend with unlocked access.
   */
  private loadUsers(): void {
    this.userService.getAllUsers(this.page).subscribe(
      data => {
        this.users = data['content'];
        this.allUsers = data['totalElements'];
        this.foundUsers = 0;
        this.pages = new Array(data['totalPages']);
      },
      error => {
        this.defaultServiceErrorHandling(error);
      }
    );
  }

  private setPage(i, event: any) {
    event.preventDefault();
    if (i > this.paginationLimitUp) {
      this.paginationLimitUp += 15;
      this.paginationLimitDown += 15;
    }
    if (i < this.paginationLimitDown) {
      this.paginationLimitUp -= 15;
      this.paginationLimitDown -= 15;
    }
    this.page = i;
    this.loadUsers();
  }

  /**
   * Locks access to given user account.
   */
  private lockUser(user: User) {
    this.editStatus[this.users.indexOf(user)] = false;
    this.userService.lockUser(user).subscribe(
      () => {this.loadUsers();
      },
      error => {
        this.defaultServiceErrorHandling(error);
      }
    );
  }

  /**
   * Unlocks access to given user account.
   */
  private unlockUser(user: User) {
    this.editStatus[this.users.indexOf(user)] = false;
    this.userService.unlockUser(user).subscribe(
      () => {this.loadUsers();
      },
      error => {
        this.defaultServiceErrorHandling(error);
      }
    );
  }

  /**
   * Searches for user which email address contains given criteria.
   */
  private searchUser(user: string) {
    this.userService.searchUser(user, 0).subscribe(
      data => {
        console.log(data);
        this.users = data['content'];
        this.foundUsers = data['totalElements'];
        this.pages = new Array(data['totalPages']);
      },
      error => {
        this.defaultServiceErrorHandling(error);
      });
  }

  /**
   * Changes user password.
   */
  private changePassword(user: User) {
    this.submitted[this.users.indexOf(user)] = true;
    if (this.passwordForm.valid) {
      user.password = this.passwordForm.controls.password.value;
      this.userService.updateProfile(user).subscribe( (user2: User) => {
        console.log('Successfully changed password of' + user2.email);
        this.changed[this.users.indexOf(user)] = true;
      },
        error => {
          this.defaultServiceErrorHandling(error);
        });
      this.editPassword[this.users.indexOf(user)] = false;
      this.clearForm();
    } else {
      console.log('Invalid input');
    }
  }

  /**
   * Validator checks if password and confirmed password match.
   */
  private checkPassword(controlName: string, matchingControlName: string) {
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
   * Sets value if a change of password is requested.
   */
  private requestChangePassword(user: User, change: boolean): void {
    this.editPassword[this.users.indexOf(user)] = change;
  }

  /**
   * Sets value if a change of status is requested.
   */
  private requestChangeStatus(user: User, change: boolean): void {
    this.editStatus[this.users.indexOf(user)] = change;
  }

  /**
   * Sets search value.
   */
  private requestSearch(value: boolean) {
    this.searched = value;
  }

  /**
   * Returns true if the authenticated user is an admin
   */
  private isAdmin(): boolean {
    return this.authService.getUserRole() === 'ADMIN';
  }

  private clearForm() {
    this.passwordForm.reset();
    for (let i = 0; i < this.submitted.length; i++) {
      console.log(this.submitted[i] = false);
    }
  }

  /**
   * Error flag will be deactivated, which clears the error message
   */
  private vanishError() {
    this.error = false;
  }

  /**
   * Error flag will be deactivated, which clears the error message
   */
  private vanishAlert(user: User) {
    this.submitted[this.users.indexOf(user)] = false;
    this.changed[this.users.indexOf(user)] = false;
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
}
