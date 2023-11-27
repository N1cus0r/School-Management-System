import * as Yup from "yup";

export const UPDATE_COMMENT_VALIDATION_SCHEMA = {
  text: Yup.string().required("This field is required"),
};

export const CREATE_COMMENT_VALIDATION_SCHEMA = {
  ...UPDATE_COMMENT_VALIDATION_SCHEMA,
  studentId: Yup.number().required("This field is required").min(1),
};
