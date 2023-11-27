import CreateButton from "../shared/CreateButton.tsx";
import CustomDrawer from "../shared/CustomDrawer.tsx";
import {useDisclosure} from "@chakra-ui/react";
import useCreateGradeForm from "../../hooks/grade/useCreateGradeForm.ts";
import {useParams} from "react-router-dom";
import GradeForm from "./GradeForm.tsx";
import {FormType} from "../../entities/FormType.ts";

const CreateGradeButton = () => {
  const { isOpen, onOpen, onClose } = useDisclosure();
  const { courseId } = useParams();
  const createGradeForm = useCreateGradeForm(courseId!, onClose);

  return (
    <>
      <CreateButton onClick={onOpen}>Add Grade</CreateButton>
      <CustomDrawer
        isOpen={isOpen}
        onClose={onClose}
        heading={"Create a Grade"}
      >
        <GradeForm
          formType={FormType.CREATE}
          form={createGradeForm}
          submitButtonText={"Create"}
        />
      </CustomDrawer>
    </>
  );
};
export default CreateGradeButton;
