package apimodels;

public class LocationUpdateResponse {


    /**
     * response : {"error":{"error_code":0,"error_msg":"","msg":"location updates successfully"}}
     */

    private ResponseBean response;

    public ResponseBean getResponse() {
        return response;
    }

    public void setResponse(ResponseBean response) {
        this.response = response;
    }

    public static class ResponseBean {
        /**
         * error : {"error_code":0,"error_msg":"","msg":"location updates successfully"}
         */

        private ErrorBean error;

        public ErrorBean getError() {
            return error;
        }

        public void setError(ErrorBean error) {
            this.error = error;
        }

        public static class ErrorBean {
            /**
             * error_code : 0
             * error_msg :
             * msg : location updates successfully
             */

            private int error_code;
            private String error_msg;
            private String msg;

            public int getError_code() {
                return error_code;
            }

            public void setError_code(int error_code) {
                this.error_code = error_code;
            }

            public String getError_msg() {
                return error_msg;
            }

            public void setError_msg(String error_msg) {
                this.error_msg = error_msg;
            }

            public String getMsg() {
                return msg;
            }

            public void setMsg(String msg) {
                this.msg = msg;
            }
        }
    }
}
