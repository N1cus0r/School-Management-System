import * as Yup from "yup";
import { Course } from "../../entities/Course.ts";
import { useFormik } from "formik";
import { UPDATE_COURSE_VALIDATION_SCHEMA } from "../../schemas/course-schemas.ts";
import useUpdateCourse from "./useUpdateCourse.ts";
import { CACHE_KEY_COURSES } from "../../utils/constants.ts";

const useUpdateCourseForm = (course: Course, onSuccess: () => void) => {
  const updateCourse = useUpdateCourse(CACHE_KEY_COURSES, onSuccess);

  return useFormik({
    initialValues: { name: course.name },
    validationSchema: Yup.object(UPDATE_COURSE_VALIDATION_SCHEMA),
    onSubmit: (values) => updateCourse.mutate([course.id, values]),
  });
};

export default useUpdateCourseForm;
