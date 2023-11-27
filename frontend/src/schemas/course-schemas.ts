import * as Yup from "yup";
export const UPDATE_COURSE_VALIDATION_SCHEMA = {
  name: Yup.string().required("This field is required"),
};

export const CREATE_COURSE_VALIDATION_SCHEMA = {
  ...UPDATE_COURSE_VALIDATION_SCHEMA,
  teacherId: Yup.string()
    .required("This field is required")
    .matches(/^[1-9]\d*$/, "Must be a positive natural number"),
};

export const COURSE_STUDENT_OPERATION = {
  studentId: Yup.number().required("This field is required").min(1),
}
