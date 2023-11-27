import * as Yup from "yup";

export const UPDATE_GRADE_VALIDATION_SCHEMA = {
  value: Yup.number().required("This field is required").min(1),
  text: Yup.string().required("This field is required"),
};

export const CREATE_GRADE_VALIDATION_SCHEMA = {
  ...UPDATE_GRADE_VALIDATION_SCHEMA,
  studentId: Yup.number().required("This field is required").min(1),
};
