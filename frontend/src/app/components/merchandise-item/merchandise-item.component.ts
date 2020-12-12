import {Component, Input, OnInit} from '@angular/core';
import {Merchandise} from '../../dtos/merchandise';
import {Router} from '@angular/router';
import {UserService} from '../../services/user.service';
import {User} from '../../dtos/user';
import {NgbModal} from '@ng-bootstrap/ng-bootstrap';
import {MerchandiseDetailComponent} from '../merchandise-detail/merchandise-detail.component';
import {DomSanitizer} from '@angular/platform-browser';
import {MerchandiseService} from '../../services/merchandise.service';
import {PhotosService} from '../../services/photos.service';

@Component({
  selector: 'app-merchandise-item',
  templateUrl: './merchandise-item.component.html',
  styleUrls: ['./merchandise-item.component.css']
})
export class MerchandiseItemComponent implements OnInit {
  @Input() buyOption: boolean;
  @Input() showAllDetails: boolean;
  @Input() merchandise: Merchandise;

  bonusPoints: number;
  title: string;
  description: string;

  error: boolean = false;
  errorMessage: string = '';
  success: boolean = false;
  successMessage: string = '';

  photo: string;

  constructor(private router: Router,
              private userService: UserService,
              public modalService: NgbModal,
              private sanitizer: DomSanitizer,
              private photoService: PhotosService) { }

  ngOnInit(): void {
  }


  vanishAlert() {
    this.error = false;
    this.success = false;
  }

  openModal() {
    const modalRef = this.modalService.open(MerchandiseDetailComponent);
    modalRef.componentInstance.merchandise = this.merchandise;
    modalRef.result.then((merchandise: Merchandise) => {
      if (merchandise) {
        this.router.navigate(['/user-bonus', {boughtMerchandise: merchandise.title}]);
      }
    });
  }

  openDetailView() {
    this.router.navigate(['/merchandise-buy/' + this.merchandise.id]);
  }

  /**
   * Use sanitizer to secure image
   */
  getSanitizer() {
    return this.sanitizer.bypassSecurityTrustResourceUrl(this.merchandise.image);
  }
}
