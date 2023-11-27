import { useFormik } from "formik";
import * as Yup from "yup";
import useAuth from "./useAuth.ts";

const useLoginForm = () => {
    const {login} = useAuth()

  return useFormik({
    initialValues: {
      email: "",
      password: "",
    },
    validationSchema: Yup.object({
      email: Yup.string()
        .email("Must be a valid email")
        .required("This field is required"),
      password: Yup.string().required("This field is required"),
    }),
    onSubmit: (values) => {
      login(values)
    },

  });
};

export default useLoginForm;
