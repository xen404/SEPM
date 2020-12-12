import {ChangeDetectorRef, Component, ElementRef, OnInit, ViewChild} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {EventService} from '../../services/event.service';
import {NewsService} from '../../services/news.service';
import {NgbPaginationConfig} from '@ng-bootstrap/ng-bootstrap';
import {AuthService} from '../../services/auth.service';
import {EventDetail} from '../../dtos/event-detail';
import {DomSanitizer} from '@angular/platform-browser';
import {Location} from '@angular/common';
import * as _ from 'lodash';


@Component({
  selector: 'app-add-event',
  templateUrl: './add-event.component.html',
  styleUrls: ['./add-event.component.css']
})
export class AddEventComponent implements OnInit {

  @ViewChild('closeModal') closeModal: ElementRef;
  error: boolean = false;
  errorMessage: string = '';
  eventForm: FormGroup;
  submitted: boolean = false;
  private events: EventDetail[];
  selectedFile: File = null;
  public imagePath;
  imgURL: any;
  private id: number;
  images: string[] = [];
  private path: string;
  private eventAdded: boolean;

  imageError: string;
  isImageSaved: boolean;
  cardImageBase64: string;
  allowedTypes = ['image/png', 'image/jpeg'];

  successCreated: boolean = true;
  successCreatedMessage: string = '';


  constructor(private eventService: EventService,
              private newsService: NewsService,
              private ngbPaginationConfig: NgbPaginationConfig,
              private formBuilder: FormBuilder,
              private cd: ChangeDetectorRef,
              private authService: AuthService,
              private sanitizer: DomSanitizer,
              private location: Location) {
    this.eventForm = this.formBuilder.group({
      title: ['', [Validators.required]],
      category: ['', [Validators.required]],
      duration: ['', [Validators.required]],
      description: ['', [Validators.required]],
      image: ['', [Validators.required]],
    });
  }

  ngOnInit(): void {
  }

  addEvent() {
    this.submitted = true;
    if (this.eventForm.valid) {
      const event: EventDetail = new EventDetail(null,
        this.eventForm.controls.title.value,
        this.eventForm.controls.category.value,
        this.eventForm.controls.description.value,
        this.eventForm.controls.duration.value,
        null,
        null,
        '10:00',
        '11:00',
        null,
        null,
        null,
        false,
      );
      this.createEvent(event);
      this.clearForm();
      this.path = event.category.toLowerCase();
    } else {
      console.log('Invalid input');
    }
  }

  private clearForm() {
    this.eventForm.reset();
    this.submitted = false;
    this.isImageSaved = false;
  }

  createEvent(eventPost: EventDetail) {
    this.eventService.createEvent(eventPost).subscribe(
      (msg: EventDetail) => {
        this.id = msg.id;
        this.closeModal.nativeElement.click();
        this.successCreated = true;
        this.successCreatedMessage = 'You have successfully created the event: ' + msg.title;
      },
      error => {
        this.defaultServiceErrorHandling(error);
      },

      () => {
        this.renderImage();
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

  isAdmin(): boolean {
    return this.authService.getUserRole() === 'ADMIN';
  }

  /**
   * Save image to database
   */
  renderImage() {
    this.eventService.getLastImage().subscribe(
      (ns) => {
        if (this.selectedFile != null) {
          const fd = new FormData();
          fd.append('file', this.selectedFile, this.selectedFile.name);
          this.eventService.uploadImage(ns, fd).subscribe(res => {
            console.log(res);
          });
        }
      },
      error => {
        this.defaultServiceErrorHandling(error);
      });
  }

  /**
   * Use sanitizer to secure image
   * @param id the id of the news
   */
  getSanitizer(id: number) {
    return this.sanitizer.bypassSecurityTrustResourceUrl(this.images[id]);
  }

  /**
   * load image in FileReader
   */
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
          this.eventForm.controls.image.setValue(this.cardImageBase64);
          this.imgURL = reader.result;
        };
      };
      reader.readAsDataURL(this.selectedFile);
    }
  }


  /**
   * Error flag will be deactivated, which clears the error news
   */
  vanishError() {
    this.error = false;
  }

  vanishAlert() {
    this.successCreated = false;
    this.successCreatedMessage = '';
  }

}
