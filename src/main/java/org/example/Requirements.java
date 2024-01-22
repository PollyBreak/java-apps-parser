package org.example;

public class Requirements {
    public String cpu;
    public String os;
    public int size;


    public String gpu;
    public String ram;

    public Requirements(String cpu, String os, int size) {
        this.cpu = cpu;
        this.os = os;
        this.size = size;
        generateGpu();
        generateRam();
    }

    private void generateRam(){
        String ram="";
        int ramNumber = (int) (Math.random() * 3);
        switch (ramNumber) {
            case 0:
                ram="4";
                break;
            case 1:
                ram="8";
                break;
            case 2:
                ram="16";
                break;
            default:
                ram="4";
        }
        this.ram = ram;
    }

    private void generateGpu(){
        String gpu="";
        int gpuNumber = (int) (Math.random() * 4);
        switch (gpuNumber) {
            case 0:
                gpu="ATI HD2600 XT";
                break;
            case 1:
                gpu="NVIDIA® GeForce® GTX 550 or ATI™ Radeon™ HD 6XXX or higher";
                break;
            case 2:
                gpu="256 mb video memory, shader model 3.0+";
                break;
            case 3:
                gpu="no";
            default:
                gpu="NVIDIA Geforce GTS 450 / AMD Radeon HD 5570";
        }
        this.gpu = gpu;
    }

    @Override
    public String toString() {
        return "Requirements{" +
                "cpu='" + cpu + '\'' +
                ", os='" + os + '\'' +
                ", size=" + size +
                ", gpu='" + gpu + '\'' +
                ", ram='" + ram + '\'' +
                '}';
    }
}
