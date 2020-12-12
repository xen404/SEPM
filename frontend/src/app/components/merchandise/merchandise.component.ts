import {Component, ElementRef, OnInit, ViewChild} from '@angular/core';
import {AuthService} from '../../services/auth.service';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {Merchandise} from '../../dtos/merchandise';
import {MerchandiseService} from '../../services/merchandise.service';
import {EventSimple} from '../../dtos/eventsimple';
import {EventService} from '../../services/event.service';
import * as _ from 'lodash';
import {PhotosService} from '../../services/photos.service';
import {ActivatedRoute, Router} from '@angular/router';

@Component({
  selector: 'app-merchandise',
  templateUrl: './merchandise.component.html',
  styleUrls: ['./merchandise.component.css']
})

export class MerchandiseComponent implements OnInit {

  @ViewChild('closeModal') closeModal: ElementRef;

  error: boolean = false;
  errorMessage: string = '';

  submitted: boolean = false;

  merchandiseItems: Merchandise[];
  images: string[] = [];
  events: EventSimple[];

  page: number = 0;
  pages: number[];
  paginationLimitUp: number = 15;
  paginationLimitDown: number = 0;
  pageSize: number = 15;
  merchandiseNum: number;

  createMerchForm:  FormGroup;
  // id of the merchandise item that is being created
  id: number;
  selectedFile: File = null;
  imagePath;
  imgURL;

  imageError: string;
  isImageSaved: boolean;
  cardImageBase64: string;

  allowedTypes = ['image/png', 'image/jpeg'];

  successCreated: boolean = false;
  createdMerchandiseMessage: string;

  constructor(private authService: AuthService,
              private router: Router,
              private formBuilder: FormBuilder,
              private merchandiseService: MerchandiseService,
              private eventService: EventService,
              private photoService: PhotosService,
              private route: ActivatedRoute) {
    this.router.routeReuseStrategy.shouldReuseRoute = () => false;
    this.createMerchForm = this.formBuilder.group( {
      title: ['', [Validators.required]],
      description: ['', [Validators.required]],
      price: ['', [Validators.required, Validators.min(0), Validators.pattern('^-?[0-9]\\d*(\\.\\d{1,2})?$')]],
      bonusPoints: ['', [Validators.required, Validators.min(0), Validators.pattern('^[0-9]*$')]],
      isBonus: [false],
      event: ['', [Validators.required]],
      image: ['', [Validators.required]]
    });
  }

  ngOnInit(): void {
    this.loadMerchItems();
    this.loadAllEvents();
    const successString  = this.route.snapshot.paramMap.get('createdMerchandise');
    if (successString != null) {
      this.successCreated = true;
      this.createdMerchandiseMessage = 'You have successfully created the merchandise item: ' + successString;
    }
  }

  loadMerchItems(): void {
    this.merchandiseService.getAllMerchandise(this.page).subscribe(
      data => {
        this.merchandiseItems = data['content'];
        this.merchandiseNum = data['totalElements'];
        this.pages = new Array(data['totalPages']);

        for (const m of this.merchandiseItems) {
          if (!this.images[m.id]) {
            this.getImage(m.id);
          } else {
            m.imagePresent = true;
            m.image = this.images[m.id];
          }
        }
      }, error => {
        this.defaultServiceErrorHandling(error);
      }
    );
  }

  /**
   * Loads all (simple) events
   */
  loadAllEvents() {
    this.eventService.getAllSimpleEvents().subscribe(
      (events: EventSimple[]) => {
        this.events = events;
      },
      error => {
        this.defaultServiceErrorHandling(error);
      }
    );
  }

  onFileSelected(event) {
    this.imageError = null;
    if (event.target.files && event.target.files[0]) {
      this.selectedFile = <File>event.target.files[0];


      if (!_.includes(this.allowedTypes, this.selectedFile.type)) {
        console.log('Only images are allowed (JPEG/PNG)');
        this.imageError = 'Only images are allowed (JPEG/PNG)';
        return false;
      }

      const reader = new FileReader();
      this.imagePath = event.target.files;
      reader.onload = (e: any) => {
        const img = new Image();
        img.src = e.target.result;
        img.onload = rs => {

          this.cardImageBase64 = e.target.result;
          this.isImageSaved = true;
          this.createMerchForm.controls.image.setValue(this.cardImageBase64);
          this.imgURL = reader.result;
        };
      };
      reader.readAsDataURL(this.selectedFile);
    }
  }

  /**
   * Save image to database
   */
  renderImage() {
    this.merchandiseService.getIndexForImage().subscribe(
      (index: number) => {
        if (this.selectedFile != null) {
          const fd = new FormData();
          fd.append('file', this.selectedFile, this.selectedFile.name);
          this.merchandiseService.uploadImage(index, fd).subscribe(res => {
            console.log(res);
          });
        }
      },
      error => {
        this.defaultServiceErrorHandling(error);
      });
  }

  /**
   * Save image with specific id to image array
   * @param id the id of the news
   */
  getImage(id: number) {
    this.merchandiseService.getImages(id).subscribe(
      (res) => {
        this.images[id] = res;
        this.merchandiseItems.find(x => x.id === id).imagePresent = true;
        this.merchandiseItems.find(x => x.id === id).image = res;
      }, error => {
        this.defaultServiceErrorHandling(error);
      }
    );
  }

  isAdmin(): boolean {
    return this.authService.getUserRole() === 'ADMIN';
  }

  /**
   * Error flag will be deactivated, which clears the error message
   */
  vanishError() {
    this.error = false;
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

  private clearForm() {
    this.createMerchForm.reset();
    this.submitted = false;
    this.isImageSaved = false;
  }

  /**
   * Starts form validation and builds a merchandise dto for sending a creation request if the form is valid.
   * If the procedure was successful, the form will be cleared.
   */
  addMerchItem() {
    this.submitted = true;
    if (this.createMerchForm.valid) {
      const merchandise = new Merchandise(
        null,
        this.createMerchForm.controls.title.value,
        this.createMerchForm.controls.description.value,
        this.createMerchForm.controls.price.value,
        this.createMerchForm.controls.bonusPoints.value,
        this.createMerchForm.controls.event.value,
        this.createMerchForm.controls.isBonus.value,
        null,
        false
      );
      this.createMerchandise(merchandise);
      this.clearForm();
    } else {
      console.log('Invalid input');
    }
  }

  /**
   * Sends merchandise creation request
   * @param merchandise the merchandise item which should be created
   */
  createMerchandise(merchandise: Merchandise) {
    this.merchandiseService.createMerchandise(merchandise).subscribe(
      (merchItem: Merchandise) => {
        this.id = merchItem.id;
        this.closeModal.nativeElement.click();
        this.router.navigate(['/merchandise', {createdMerchandise: merchItem.title}]);
      },
      error => {
        this.defaultServiceErrorHandling(error);
      },
      () => {
        this.renderImage();
      }
    );
  }

  getRandomImage(id: number) {
    const merchandiseType = this.merchandiseItems[id].title;
    let searchTerm = '';
    if (merchandiseType.includes('shirt')) {
      searchTerm = 'shirt';
    } else if (merchandiseType.includes('Hoodie')) {
      searchTerm = 'hoodie';
    } else if (merchandiseType.includes('Poster')) {
      searchTerm = 'poster';
    } else if (merchandiseType.includes('Autograph')) {
      searchTerm = 'card';
    }
    this.photoService.getMatchingImage(searchTerm).subscribe(
      res => {
        this.images[id] = res.urls.regular;
        this.merchandiseItems.find(x => x.id === id).imagePresent = true;
        this.merchandiseItems.find(x => x.id === id).image = res.urls.regular;
      }, error => {
        this.defaultServiceErrorHandling(error);
      }
    );
  }

  private setPage(i, event: any) {
    event.preventDefault();
    this.page = i;
    if (i > this.paginationLimitUp) {
      this.paginationLimitUp += 15;
      this.paginationLimitDown += 15;
    }
    if (i < this.paginationLimitDown) {
      this.paginationLimitUp -= 15;
      this.paginationLimitDown -= 15;
    }
    this.loadMerchItems();
  }

  vanishAlert() {
    this.successCreated = false;
    this.router.navigate(['/merchandise']);
  }
}
