import * as Yup from "yup";
import { useFormik } from "formik";
import { CREATE_COURSE_VALIDATION_SCHEMA } from "../../schemas/course-schemas.ts";
import { Role } from "../../entities/user/Role.ts";
import { User } from "../../entities/user/User.ts";
import useCreateCourse from "./useCreateCourse.ts";
import { CACHE_KEY_COURSES } from "../../utils/constants.ts";

const useCreateCourseForm = (user: User, onSuccess: () => void) => {
  const createCourse = useCreateCourse(CACHE_KEY_COURSES, onSuccess);

  return useFormik({
    initialValues: {
      name: "",
      teacherId: user.role === Role.ADMIN ? 0 : user.id,
    },
    validationSchema: Yup.object(CREATE_COURSE_VALIDATION_SCHEMA),
    onSubmit: (values) => createCourse.mutate(values),
  });
};

export default useCreateCourseForm;
