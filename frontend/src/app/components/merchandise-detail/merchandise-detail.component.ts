import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {Merchandise} from '../../dtos/merchandise';
import {User} from '../../dtos/user';
import {Router} from '@angular/router';
import {UserService} from '../../services/user.service';
import {NgbActiveModal} from '@ng-bootstrap/ng-bootstrap';
import {DomSanitizer} from '@angular/platform-browser';

@Component({
  selector: 'app-merchandise-detail',
  templateUrl: './merchandise-detail.component.html',
  styleUrls: ['./merchandise-detail.component.css']
})
export class MerchandiseDetailComponent implements OnInit {
  @Input() merchandise: Merchandise;
  @Output() passEntry: EventEmitter<any> = new EventEmitter();

  error: boolean = false;
  errorMessage: string = '';

  constructor(private router: Router,
              private userService: UserService,
              public activeModal: NgbActiveModal,
              private sanitizer: DomSanitizer) { }

  ngOnInit(): void {
  }

  /**
   * Diminish the logged in users bonus points by the bonus points of the merchandise item.
   * Closes the modal view when the process is finished.
   */
  redeemBonus() {
    this.userService.redeemBonus(this.userService.getLoggedInUser().id, this.merchandise.bonusPoints).subscribe(
      (user: User) => {
        this.activeModal.close(this.merchandise);
        this.passEntry.emit(this.merchandise);
      }, error => {
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
   * Use sanitizer to secure image
   */
  getSanitizer() {
    return this.sanitizer.bypassSecurityTrustResourceUrl(this.merchandise.image);
  }
}
